/*
 * Copyright 2015 i-net software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inet.gradle.setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Simple template class for replacing placeholders in text files (e.g. scripts, config files).
 */
public class Template {
    private String template;
    
    /**
     * Creates an instance and loads the specified file from the classpath as template.
     * @param file the file in classpath relative to the package of this class
     * @throws IOException on errors during reading the file
     */
    public Template( String file ) throws IOException {
        try (InputStream in = getClass().getResourceAsStream( file )) {
            InputStreamReader reader = new InputStreamReader( in, StandardCharsets.UTF_8 );
            StringBuffer sb = new StringBuffer( in.available() );
            int chr;
            while( (chr = reader.read()) != -1 ) {
                sb.append( (char)chr );
            }
            template = sb.toString();
        }
    }
    
    /**
     * Replaces occurences of the placeholder <tt>{{NAME}}</tt> to the specified content.
     * @param placeholder the name of the placerholder
     * @param content the content to which to placeholder will be replaces
     */
    public void setPlaceholder(String placeholder, String content) {
    	if ( content == null ) {
    		content = ""; // Remove if not set.
    	}
        template = template.replace( "{{" + placeholder + "}}", content );
    }
    
    /**
     * Writes the template with replaced placeholder to the specified file.
     * @param file the file to write to
     * @throws IOException on errors during writing
     */
    public void writeTo(File file) throws IOException {
        try(FileWriter writer = new FileWriter( file )) {
            writer.write( template );
        }
    }
    
    @Override
    public String toString() {
    	return template;
    }
}
