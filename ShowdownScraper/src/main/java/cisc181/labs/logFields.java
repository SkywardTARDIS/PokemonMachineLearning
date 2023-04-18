package cisc181.labs;

import com.github.cliftonlabs.json_simple.JsonKey;

public enum logFields implements JsonKey {
    //Creating all necessary fields for the enum
    id, log;

    public String getKey() {
        //Allows use of enum to get values from jsonobject
        //Standard getKey method, takes no parameters
        //returns the corresponding string to the enum key
        return this.toString();
    }

    public Object getValue() {
        //Byproduct of implements JsonKey, unused
        return null;
    }
}
