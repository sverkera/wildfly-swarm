require 'fileutils'
require 'set'

IGNORE = Regexp.compile(
         %w{archetype
            arquillian/adapter
            arquillian/daemon
            arquillian/resources
            bootstrap
            fractionlist
            internal
            org/jboss/modules
            plugin
            runtime
            swarmtool
            tools}
         .map {|s| "/#{s}/"}
         .join("|")
)

def clean(d)
  FileUtils.rm_rf(d)
end

def collect_src(input_dir, output_dir)
  packages = Set.new
  Dir.glob("#{input_dir}/**/*.java")
    .reject {|path| IGNORE =~ path}
    .each do |path|
      _, rel_path = path.split(input_dir)
      dest = File.join(output_dir, rel_path)
      FileUtils.mkdir_p(File.dirname(dest))
      FileUtils.cp(path, dest)
      packages << File.split(rel_path).first.slice(1..-1).gsub("/", ".")
  end

  packages
end

def process(input_dir, output_dir)
  packages = {}
  metadata = {}
  Dir.glob("#{input_dir}/*").each do |dir|
    metadata_file = File.join(dir, "_metadata")
    if File.exist?(metadata_file)
      dirname = File.basename(dir)
      packages_for_module = collect_src(dir, output_dir)
      unless packages_for_module.empty?
        packages[dirname] = packages_for_module
        name, stability = File.read(metadata_file).strip.split(/::::/)
        metadata[dirname] = {:name => name, :stability => stability}
      end
    end
  end

  [packages, metadata]
end

def marshal(x, f)
  File.open(f, "w+") do |f|
    Marshal.dump(x, f)
  end
end

target_dir, output_dir, dep_src_dir = ARGV

puts "Copying dependency src to the javadoc tree"

clean(output_dir)
packages, metadata = process(dep_src_dir, output_dir)

f = File.join(target_dir, "packages.dat")
puts "Marshaling package list to #{f}"
marshal(packages, f)

f = File.join(target_dir, "metadata.dat")
puts "Marshaling metadata list to #{f}"
marshal(metadata, f)
