package com.sprint.mission.discodeit.Example;

import java.io.FileInputStream;

public class FileInputStreamExample {
    public static void main(String[] args) {
        try {
            FileInputStream fileInput = new FileInputStream("java.txt");
            int i;
            while ((i = fileInput.read()) != -1) {
                System.out.print((char) i);
            }
            fileInput.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
