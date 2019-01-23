import groovy.json.JsonSlurper

File outputFile;
def response;

outputFile = new File(basedir, "target/get-method.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.method == "GET"

outputFile = new File(basedir, "target/put-method.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.method == "PUT"

outputFile = new File(basedir, "target/post-method.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.method == "POST"
