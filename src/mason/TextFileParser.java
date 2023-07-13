package mason;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TextFileParser {
    void parseLine(String[] literals);

	public static void parse(String path, TextFileParser parser) {
		parse(path, "UTF-8", parser);
	}

    public static void parse(String path, String encodingName, TextFileParser parser) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encodingName));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove leading and trailing spaces

                if (line.isEmpty() || line.startsWith("//")) {
                    continue; // Ignore empty or commented lines
                }

                parser.parseLine(parseLiterals(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String[] parseLiterals(String input) {
        // Remove leading and trailing spaces and tabs
        String trimmedInput = input.trim();

        // The pattern will look for literals surrounded by quotes and/or separated by spaces or tabs.
        Pattern p = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher m = p.matcher(trimmedInput);

        List<String> tokens = new ArrayList<>();
        while(m.find()) {
            if(m.group(1) != null) {
                // If we found a quoted string, group(1) will be non-null, so add it to the tokens list
                tokens.add(m.group(1));
            } else {
                // Otherwise, we found an unquoted word, so add that to the tokens list
                tokens.add(m.group(2));
            }
        }

        // Convert the list of tokens to a String array and return it
        return tokens.toArray(new String[0]);
    }
}
