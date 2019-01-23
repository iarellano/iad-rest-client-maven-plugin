import groovy.json.JsonSlurper

File outputFile;
def response;

outputFile = new File(basedir, "target/multipart.json");
assert outputFile.isFile();

response = new JsonSlurper().parseFile(outputFile, "UTF-8");

assert response.form.firstName == "John"
assert response.form.lastName == "Connor"
assert response.method == "POST"