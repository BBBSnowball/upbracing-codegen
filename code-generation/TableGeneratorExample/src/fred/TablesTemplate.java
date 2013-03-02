package fred;

public class TablesTemplate implements de.upbracing.code_generation.ITemplate {
  protected static String nl;
  public static synchronized TablesTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    TablesTemplate result = new TablesTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/*" + NL + " * tables.h" + NL + " *" + NL + " * This file defines the features the the Timers should have." + NL + " * " + NL + " * NOTE: This file was generated and should not be altered manually!" + NL + " */" + NL;
  protected final String TEXT_2 = NL + NL + "typedef struct {";
  protected final String TEXT_3 = NL + "\t";
  protected final String TEXT_4 = " ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL + "} ";
  protected final String TEXT_7 = "_t;" + NL;
  protected final String TEXT_8 = NL;
  protected final String TEXT_9 = "_t ";
  protected final String TEXT_10 = "[] = {";
  protected final String TEXT_11 = NL + "};" + NL;

  /* (non-javadoc)
    * @see IGenerator#generate(Object)
    */
  public String generate(de.upbracing.code_generation.config.CodeGeneratorConfigurations config, Object generator_data)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
for (TableConfig table : config.getState(TableConfigProvider.STATE)) {

    stringBuffer.append(TEXT_2);
     for (int i=0;i<table.getNames().size();i++) { 
    stringBuffer.append(TEXT_3);
    stringBuffer.append( table.getTypes().get(i) );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( table.getNames().get(i) );
    stringBuffer.append(TEXT_5);
     } 
    stringBuffer.append(TEXT_6);
    stringBuffer.append( table.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    stringBuffer.append( table.getName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( table.getName() );
    stringBuffer.append(TEXT_10);
    
for (Object[] data : table.getData()) {
	stringBuffer.append("\n\t{ ");
	for (int j=0;j<data.length;j++) {
		if (j > 0)
			stringBuffer.append(", ");
		stringBuffer.append(data[j]);
	}
	stringBuffer.append(" },");
}

    stringBuffer.append(TEXT_11);
    
}

    return stringBuffer.toString();
  }
}
