import groovy.json.JsonSlurper

File outputFile;
def response;

outputFile = new File(basedir, "target/payload-test.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.json.firstName == "John"
assert response.json.lastName == "Connor"
assert response.headers["Content-Type"] == "application/json"
assert response.method == "POST"