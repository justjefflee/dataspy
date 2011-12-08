package com.dataspy.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.MultipleSchemaAnalyzer;
import net.sourceforge.schemaspy.SchemaAnalyzer;
import net.sourceforge.schemaspy.model.ConnectionFailure;
import net.sourceforge.schemaspy.model.Database;
import net.sourceforge.schemaspy.model.EmptySchemaException;
import net.sourceforge.schemaspy.model.InvalidConfigurationException;
import net.sourceforge.schemaspy.model.Table;
import net.sourceforge.schemaspy.model.xml.SchemaMeta;
import net.sourceforge.schemaspy.util.ConnectionURLBuilder;
import net.sourceforge.schemaspy.util.DbSpecificOption;
import net.sourceforge.schemaspy.util.LineWriter;
import net.sourceforge.schemaspy.util.PasswordReader;

class MySchemaAnalyzer extends SchemaAnalyzer {
    	
    public Database analyze(Config config) throws Exception {
        try {
            if (config.isHelpRequired()) {
                //config.dumpUsage(null, false);
                return null;
            }

            if (config.isDbHelpRequired()) {
                //config.dumpUsage(null, true);
                return null;
            }

            long start = System.currentTimeMillis();
            long startDiagrammingDetails = start;
            long startSummarizing = start;

            File outputDir = config.getOutputDir();
            if (!outputDir.isDirectory()) {
                if (!outputDir.mkdirs()) {
                    throw new IOException("Failed to create directory '" + outputDir + "'");
                }
            }

            List<String> schemas = config.getSchemas();
            if (schemas != null) {
                List<String> args = config.asList();

                // following params will be replaced by something appropriate
                yankParam(args, "-o");
                yankParam(args, "-s");
                args.remove("-all");
                args.remove("-schemas");
                args.remove("-schemata");

                String dbName = config.getDb();

                MultipleSchemaAnalyzer.getInstance().analyze(dbName, schemas, args, config.getUser(), outputDir, config.getCharset(), Config.getLoadedFromJar());
                return null;
            }

            Properties properties = config.getDbProperties(config.getDbType());

            ConnectionURLBuilder urlBuilder = new ConnectionURLBuilder(config, properties);
            if (config.getDb() == null)
                config.setDb(urlBuilder.getConnectionURL());

            if (config.getRemainingParameters().size() != 0) {
                StringBuilder msg = new StringBuilder("Unrecognized option(s):");
                for (String remnant : config.getRemainingParameters())
                    msg.append(" " + remnant);
            }

            String driverClass = properties.getProperty("driver");
            String driverPath = properties.getProperty("driverPath");
            if (driverPath == null)
                driverPath = "";
            if (config.getDriverPath() != null)
                driverPath = config.getDriverPath() + File.pathSeparator + driverPath;

            Connection connection = getConnection(config, urlBuilder.getConnectionURL(), driverClass, driverPath);

            DatabaseMetaData meta = connection.getMetaData();
            String dbName = config.getDb();
            String schema = config.getSchema();

            if (config.isEvaluateAllEnabled()) {
                List<String> args = config.asList();
                for (DbSpecificOption option : urlBuilder.getOptions()) {
                    if (!args.contains("-" + option.getName())) {
                        args.add("-" + option.getName());
                        args.add(option.getValue().toString());
                    }
                }

                yankParam(args, "-o");  // param will be replaced by something appropriate
                yankParam(args, "-s");  // param will be replaced by something appropriate
                args.remove("-all");    // param will be replaced by something appropriate

                String schemaSpec = config.getSchemaSpec();
                if (schemaSpec == null)
                    schemaSpec = properties.getProperty("schemaSpec", ".*");
                MultipleSchemaAnalyzer.getInstance().analyze(dbName, meta, schemaSpec, null, args, config.getUser(), outputDir, config.getCharset(), Config.getLoadedFromJar());
                return null;    // no database to return
            }

            if (schema == null && meta.supportsSchemasInTableDefinitions() &&
                    !config.isSchemaDisabled()) {
                schema = config.getUser();
                if (schema == null)
                    throw new InvalidConfigurationException("Either a schema ('-s') or a user ('-u') must be specified");
                config.setSchema(schema);
            }

            SchemaMeta schemaMeta = config.getMeta() == null ? null : new SchemaMeta(config.getMeta(), dbName, schema);
            if (config.isHtmlGenerationEnabled()) {
                new File(outputDir, "tables").mkdirs();
                new File(outputDir, "diagrams/summary").mkdirs();
            }

            //
            // create our representation of the database
            //
            Database db = new Database(config, connection, meta, dbName, schema, properties, schemaMeta);

            schemaMeta = null; // done with it so let GC reclaim it

            LineWriter out;
            Collection<Table> tables = new ArrayList<Table>(db.getTables());
            tables.addAll(db.getViews());

            if (tables.isEmpty()) {
                if (!config.isOneOfMultipleSchemas()) // don't bail if we're doing the whole enchilada
                    throw new EmptySchemaException();
            }

            return db;
        } catch (Config.MissingRequiredParameterException missingParam) {
            return null;
        }
    }

    private Connection getConnection(Config config, String connectionURL,
                      String driverClass, String driverPath) throws FileNotFoundException, IOException {
        List<URL> classpath = new ArrayList<URL>();
        List<File> invalidClasspathEntries = new ArrayList<File>();
        StringTokenizer tokenizer = new StringTokenizer(driverPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File pathElement = new File(tokenizer.nextToken());
            if (pathElement.exists())
                classpath.add(pathElement.toURI().toURL());
            else
                invalidClasspathEntries.add(pathElement);
        }

        URLClassLoader loader = new URLClassLoader(classpath.toArray(new URL[classpath.size()]));
        Driver driver = null;
        try {
            driver = (Driver)Class.forName(driverClass, true, loader).newInstance();

            // have to use deprecated method or we won't see messages generated by older drivers
            //java.sql.DriverManager.setLogStream(System.err);
        } catch (Exception exc) {
            System.err.println(exc); // people don't want to see a stack trace...
            System.err.println();
            System.err.print("Failed to load driver '" + driverClass + "'");
            if (classpath.isEmpty())
                System.err.println();
            else
                System.err.println("from: " + classpath);
            if (!invalidClasspathEntries.isEmpty()) {
                if (invalidClasspathEntries.size() == 1)
                    System.err.print("This entry doesn't point to a valid file/directory: ");
                else
                    System.err.print("These entries don't point to valid files/directories: ");
                System.err.println(invalidClasspathEntries);
            }
            System.err.println();
            System.err.println("Use the -dp option to specify the location of the database");
            System.err.println("drivers for your database (usually in a .jar or .zip/.Z).");
            System.err.println();
            throw new ConnectionFailure(exc);
        }

        Properties connectionProperties = config.getConnectionProperties();
        if (config.getUser() != null) {
            connectionProperties.put("user", config.getUser());
        }
        if (config.getPassword() != null) {
            connectionProperties.put("password", config.getPassword());
        } else if (config.isPromptForPasswordEnabled()) {
            connectionProperties.put("password",
                    new String(PasswordReader.getInstance().readPassword("Password: ")));
        }

        Connection connection = null;
        try {
            connection = driver.connect(connectionURL, connectionProperties);
            if (connection == null) {
                System.err.println();
                System.err.println("Cannot connect to this database URL:");
                System.err.println("  " + connectionURL);
                System.err.println("with this driver:");
                System.err.println("  " + driverClass);
                System.err.println();
                throw new ConnectionFailure("Cannot connect to '" + connectionURL +"' with driver '" + driverClass + "'");
            }
        } catch (UnsatisfiedLinkError badPath) {
            System.err.println();
            System.err.println("Failed to load driver [" + driverClass + "] from classpath " + classpath);
            System.err.println();
            System.err.println("Make sure the reported library (.dll/.lib/.so) from the following line can be");
            System.err.println("found by your PATH (or LIB*PATH) environment variable");
            System.err.println();
            badPath.printStackTrace();
            throw new ConnectionFailure(badPath);
        } catch (Exception exc) {
            System.err.println();
            System.err.println("Failed to connect to database URL [" + connectionURL + "]");
            System.err.println();
            exc.printStackTrace();
            throw new ConnectionFailure(exc);
        }

        return connection;
    }
    private void yankParam(List<String> args, String paramId) {
        int paramIndex = args.indexOf(paramId);
        if (paramIndex >= 0) {
            args.remove(paramIndex);
            args.remove(paramIndex);
        }
    }
}
	