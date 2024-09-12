package exercise;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class XMLToJSONConverter {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		// Prompt the user to enter the absolute path of the XML file
		System.out.print("Enter the absolute path of the XML file: ");
		String xmlFilePath = scanner.nextLine();

		// Prompt the user to enter the name of a attribute in the XML file
		System.out.print("Enter the name of the attribute you want to print its value: ");
		String attributeName = scanner.nextLine();

		try {

			JSONObject jsonObject = convertXMLFileToJSON(xmlFilePath);
			System.out.println("JSON Output:");
			System.out.println(jsonObject.toString(4)); // Pretty print (4-spaces indentation)

			Object jsonattributeValue = getJSONattributeValue(jsonObject, attributeName);
			if (jsonattributeValue == null || jsonattributeValue.equals("")) {
				System.out.println("JSON attribute " + attributeName + " does not exist!");
			} else {
				System.out.println("JSON attribute value = " + jsonattributeValue.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	public static JSONObject convertXMLFileToJSON(String filePath) throws Exception {

		Path path = Paths.get(filePath);
		String fileContent = Files.readString(path);
		return convertXMLToJSON(fileContent);
	}

	public static JSONObject convertXMLToJSON(String xml) throws Exception {

		return XML.toJSONObject(xml);
	}

	private static Object getJSONattributeValue(JSONObject jsonObject, String attributeName) {

		// If the attributeName is a value of an attribute, return the value of its sibling value attribute 
		if (jsonObject.has("Name") && jsonObject.has("Value")) {
			if (jsonObject.get("Name").toString().equalsIgnoreCase(attributeName)) {
				return jsonObject.get("Value");
			}

		}

		// Try searching in the direct child nodes
		if (jsonObject.has(attributeName)) {
			return jsonObject.get(attributeName);
		}

		// Traverse through all the keys in the JSONObject
		for (String key : jsonObject.keySet()) {
			Object value = jsonObject.get(key);

			// If the value is another JSONObject, search recursively
			if (value instanceof JSONObject) {
				Object result = getJSONattributeValue((JSONObject) value, attributeName);
				if (result != null) {
					return result;
				}
			}

			if (value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) value;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject nextJsonObject = jsonArray.getJSONObject(i);
					Object result = getJSONattributeValue(nextJsonObject, attributeName);
					if (result != null) {
						return result;
					}
				}
			}
		}

		return null; // Return null if the attribute is not found
	}
}
