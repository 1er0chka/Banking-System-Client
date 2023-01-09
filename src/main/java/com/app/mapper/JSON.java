package com.app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class JSON<T> {
    public T fromJson(String data, Class<T> valueType) throws JsonProcessingException {
        return (new ObjectMapper()).readValue(data, valueType);
    }

    public String toJson(T value, Class<T> valueType) throws IOException {
        StringWriter writer = new StringWriter();
        (new ObjectMapper()).writeValue(writer, value);
        return writer.toString();
    }

    public ArrayList<T> fromJsonArray(String data, Class<T> valueType) throws JsonProcessingException {
        ArrayList<T> arrayList = new ArrayList<>();

        JSONArray jsonArray = (JSONArray) new JSONTokener(data).nextValue();
        for (int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(new ObjectMapper().readValue(jsonArray.get(i).toString(), valueType));
        }
        return arrayList;
    }
}
