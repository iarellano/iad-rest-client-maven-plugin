import groovy.json.JsonSlurper

File outputFile;
def response;

outputFile = new File(basedir, "target/path-parameters.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.url == "https://httpbin.org/anything/second-level-path/third-level-path"