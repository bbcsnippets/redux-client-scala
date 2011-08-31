temp = "../temp"
puts `git clone -l -s -b gh-pages . #{temp}`
if system("mvn release:perform")
    puts `for DIR in $(find #{temp} -type d); do
      (
        echo -e "<html>\\n<body>\\n<h1>Directory listing</h1>\\n<hr/>\\n<pre>"
        ls -1pa "${DIR}" | grep -v "^\\./$" | grep -v "^index\\.html$" | awk '{ printf "<a href=\\"%s\\">%s</a>\\n",$1,$1 }'
        echo -e "</pre>\\n</body>\\n</html>"
      ) > "${DIR}/index.html"
    done`
    puts `cd #{temp} && git add -A && git commit -m "releasing artifacts" && git push origin gh-pages`
    puts `git push origin gh-pages`
    puts " artifact released successfully "
else
  puts " could not release artifact "
end
puts `rm -rdf #{temp};true`