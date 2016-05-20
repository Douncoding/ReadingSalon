package com.douncoding.readingsalon.orm;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "com.douncoding.readingsalon.dao");
        schema.enableActiveEntitiesByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, "../app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity content = addContent(schema);
        Entity member = addMember(schema);
    }

    private static Entity addContent(final Schema schema) {
        Entity content = schema.addEntity("Contents");

        content.addIdProperty().primaryKey();
        content.addIntProperty("type");
        content.addStringProperty("title");
        content.addStringProperty("content");
        content.addStringProperty("subject");
        content.addStringProperty("overview");
        content.addStringProperty("image");
        content.addIntProperty("view");
        content.addDateProperty("createAt");
        content.addDateProperty("updateAt");

        return content;
    }

    private static Entity addMember(final Schema schema) {
        Entity member = schema.addEntity("Member");

        member.addIdProperty().primaryKey();
        member.addStringProperty("name");
        member.addStringProperty("email");
        member.addStringProperty("password");
        member.addIntProperty("writer");

        return member;
    }
}
