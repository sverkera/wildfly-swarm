require 'fileutils'
require 'set'

def read_overview_frame(path)
  content = {:pre => [], :packages => [], :post => []}
  position = :pre
  
  File.readlines(path).each do |line|
    case line
    when /ul title/
      position = :packages
    when /\/ul/
      position = :post
    else
      content[position] << line
    end
  end

  content
end

def read_overview_summary(path)
  content = {:pre => [], :packages => [], :post => []}
  position = :pre
  
  lines = File.readlines(path)
  idx = 0
  while (idx < lines.count)
    case lines[idx]
    when /table/
      position = :packages
      idx += 7
    when /\/tbody/
      position = :post
      idx += 2
    else
      if position == :packages
        package = ""
        4.times do
          package += lines[idx]
          idx += 1
        end
        content[position] << package
      else 
        content[position] << lines[idx]
        idx += 1
      end
    end
    
  end

  content
end

def group_packages(packages, groupings)
  result = Hash.new { |h, k| h[k] = [] }
  package_matchers = groupings.reduce({}) do |acc, (k, v)|
    acc[k] = Regexp.compile(v.map {|s| ">#{s.gsub(".", "\\.")}<"}.join("|"))
    acc
  end
  
  packages.each do |p|
    package_matchers.each do |g, matcher|
      result[g] << p if matcher.match(p)
    end
  end

  result
end

def write_content(content, metadata, path)
  File.open(path, "w") do |f|
    content[:pre].each {|line| f.puts(line)}
    
    content[:packages].keys.sort do |x,y|
      if x == "config-api"
        1
      elsif y == "config-api"
        -1
      else
        x <=> y
      end
    end.each do |key|
      yield(f, metadata[key][:name], metadata[key][:stability], content[:packages][key])
    end
    
    content[:post].each {|line| f.puts(line)}
  end
end

def stability_badge(level)
  if level
    level = level.downcase
    %Q{<img src="http://badges.github.io/stability-badges/dist/#{level}.svg" alt="[#{level}]"/>}
  end
end

target_path, doc_path = $ARGV
doc_path << "/apidocs"

puts "Grouping packages for javadoc"

module_packages = Marshal.load(File.open(File.join(target_path,
                                                   "packages.dat")))

module_metadata = Marshal.load(File.open(File.join(target_path,
                                                   "metadata.dat")))

content = read_overview_frame("#{doc_path}/overview-frame.html")
content[:packages] = group_packages(content[:packages], module_packages)
write_content(content, module_metadata,
              "#{doc_path}/overview-frame.html") do |f, title, stability, packages|
  f.puts "<h4>#{title}<div>#{stability_badge(stability)}</div></h4><ul>"
  packages.each {|line| f.puts(line)}
  f.puts "</ul>"
end

content = read_overview_summary("#{doc_path}/overview-summary.html")
content[:packages] = group_packages(content[:packages], module_packages)
write_content(content, module_metadata,
              "#{doc_path}/overview-summary.html") do |f, title, stability, packages|
  f.puts %Q{<table class="overviewSummary" border="0" cellpadding="3" cellspacing="0" summary="#{title} table, listing packages, and an explanation">
<caption><span style="background-color: #ffffff; padding-left: 0">#{title} #{stability_badge(stability)}</span><span class="tabEnd">&nbsp;</span></caption>
<tr>
<th class="colFirst" scope="col">Package</th>
<th class="colLast" scope="col">Description</th>
</tr>
<tbody>}
  packages.each {|line| f.puts(line)}
  f.puts "</tbody></table>"
end
