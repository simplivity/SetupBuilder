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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.gradle.api.internal.file.FileResolver;
import org.gradle.process.internal.DefaultExecAction;

import com.inet.gradle.setup.util.IndentationOutputStream;

/**
 * Some basic builder functionally.
 * 
 * @author Volker Berlin
 *
 * @param <T> the task
 */
public abstract class AbstractBuilder<T extends AbstractSetupTask> {

    protected T            task;

    protected FileResolver fileResolver;

    protected SetupBuilder setup;

    protected File         buildDir;

    protected AbstractBuilder( T task, SetupBuilder setup, FileResolver fileResolver ) {
        this.task = task;
        this.setup = setup;
        this.fileResolver = fileResolver;
        this.buildDir = task.getTemporaryDir();
    }

    /**
     * Call a program from the WIX installation.
     * 
     * @param parameters the parameters
     */
    protected void exec( ArrayList<String> parameters ) {
        exec( parameters, null, null );
    }

    protected void exec( ArrayList<String> parameters, InputStream input, OutputStream output ) {
    	exec( parameters, input, output, false);
    }

    /**
     * Execute an external process.
     * 
     * @param parameters command line
     * @param input optional InputStream for the process
     * @param output optional OutputStream for the process
     * @param ignoreExitValue true, does not throw an exception if the return code is not equals sero.
     */
    @SuppressWarnings( "resource" )
    protected void exec( ArrayList<String> parameters, InputStream input, OutputStream output, boolean ignoreExitValue ) {
        // print command line to the log
        StringBuilder log = new StringBuilder( "\t" );
        for( String para : parameters ) {
        	
        	if ( para == null ) {
        		task.getProject().getLogger().lifecycle( "Parameter not set. This will fail now:" + log.toString() );
        	}
        	
            log.append( '\"' ).append( para );
            if( para.endsWith( "\\" ) ) {
                log.append( '\\' );
            }
            log.append( "\" " );
        }
        task.getProject().getLogger().lifecycle( log.toString() );

        DefaultExecAction action = new DefaultExecAction( fileResolver );
        action.setCommandLine( parameters );
        action.setIgnoreExitValue( ignoreExitValue );
        action.setWorkingDir( buildDir );
        if( input != null ) {
            action.setStandardInput( input );
        }
        if( output == null ) {
            output = new IndentationOutputStream( System.out );
        }
        action.setStandardOutput( output );
        try {
            action.execute();
            output.flush();
        } catch( Throwable th ) {
            throw new RuntimeException( th );
        }
    }
}
