package perestroika;

import java.util.ArrayList;


public class Record implements Comparable {

	public ArrayList<String> names = new ArrayList<String>();
	public ArrayList<Object> values = new ArrayList<Object>();
    
	
    public Record() {
        // No constructor
    }
    
    
    private int findForWrite(String name) {
    
    	for (int i = 0; i < names.size(); i++) {
    		if (names.get(i).equals(name)) return i;
    	}
    	return -1;
    }
    
    
    private int findForRead(String name) {
    
    	for (int i = 0; i < names.size(); i++) {
    		if (names.get(i).equals(name)) return i;
    	}
    	try {
    		throw new IllegalStateException("No field named [" + name + "]!");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return -1;
    }

    
    public void setString(String name, String value) {
    	int i = findForWrite(name);
    	if (i >= 0) {
    		values.set(i, value);
    	} else {
    		names.add(name);
    		values.add(value);
    	}
    }
    
    
    public String getString(String name) {

    	int i = findForRead(name);
    	String value = (String) values.get(i);

    	try {
    		return value;
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println("Error fetching [" + name + "].");
    		System.out.println("Value was [" + value + "].");
    	}
    	return "Exception thrown, data not returned.";
    }


    @Override
    public int compareTo(Object other) {

      String ourKey = (String) this.values.get(0);
      String theirKey = (String) ((Record) other).values.get(0);
      return ourKey.compareTo(theirKey);
   }
   
}



