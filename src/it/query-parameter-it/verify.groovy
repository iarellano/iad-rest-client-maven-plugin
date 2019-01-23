import groovy.json.JsonSlurper

File outputFile;
def response;

outputFile = new File(basedir, "target/query-parameters.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.args.firstName == "John"
assert response.args.lastName == "Connor"
assert response.method == "GET"