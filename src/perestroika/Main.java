package perestroika;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;




public class Main {

    public static void main(String[] args) {
    	new Main();
    }
    
    
    void log(String s) {
    	System.out.println(s);
    }
    
    
    private String load(File f) {
    	
    	StringBuffer buf = new StringBuffer();
    	
    	try {
    		InputStream in;
    		BufferedReader reader;
    		in = new FileInputStream(f);
    		reader = new BufferedReader(new InputStreamReader(in));

    		String line = null;

    		while (true) {
    			line = reader.readLine();
    			if (line == null) break;
    			buf.append(line + "\r\n");
    		}

    		reader.close();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return buf.toString();
    }
    
    
    
    public Main() {
    	
    	try {

    		File baseDir = null;
    		File articleDir = null;
    		File destDir = null;
    		File themeDir = null;
    		

    		// Determine directories
    		baseDir = new java.io.File(".");
    		log("DIR: " + baseDir.getCanonicalPath());


    		articleDir = new File(baseDir + baseDir.separator + "articles");
    		destDir = new File(baseDir + baseDir.separator + "docs");
    		themeDir = new File(baseDir + baseDir.separator + "theme");

    		String header = load(new File(themeDir + baseDir.separator + "header.txt"));
    		String footer = load(new File(themeDir + baseDir.separator + "footer.txt"));
    		String separator = load(new File(themeDir + baseDir.separator + "separator.txt"));

    		
    		log("ARTICLES: " + articleDir);


    		// Prepare to gather articles into RAM.
    		ArrayList<Record> articles = new ArrayList<Record>();

    		
    		// Scan articles directory for files.
    		File[] files = articleDir.listFiles();
    		
    		for (int i = 0; i < files.length; i++) {
    			log("ARTICLE: " + files[i]);
    			
    			if (files[i].isDirectory()) continue;

    			// Open and read file.
    			InputStream artIn;
    			BufferedReader artReader;

    			artIn = new FileInputStream(files[i]);
    			artReader = new BufferedReader(new InputStreamReader(artIn));

    			Record article = new Record();
    			
    			// Swap filename from TXT to HTML early on.
    			article.setString("filename", files[i].getName().replaceAll(".txt", ".html"));
    			
    			article.setString("title", artReader.readLine());
    			article.setString("date", artReader.readLine());
    			article.setString("author", artReader.readLine());
    			article.setString("keywords", artReader.readLine());
                
    			log("TITLE: " + article.getString("title"));
    			log("DATE: " + article.getString("date"));
    			log("AUTHOR: " + article.getString("author"));
    			log("KEYWORDS: " + article.getString("keywords"));
    			
    			StringBuffer content = new StringBuffer();
    			String line = null;
    			
                while (true) {
                	line = artReader.readLine();
                	if (line == null) break;
                	content.append(line + "\r\n");
                }
                artReader.close();
                
                article.setString("content", content.toString());
                
                // Add article into arraylist
                articles.add(article);
    		}

    		
    		Collections.sort(articles);

    		
    		
    		
    		
        	// Clear all articles from destination directory!
    		File[] destFiles = destDir.listFiles();
    		for (int i = 0; i < destFiles.length; i++) {
    			log("DEST: " + destFiles[i].getName());
    			if (destFiles[i].getName().matches("^[0-9]{4}[-][-.0-9a-zA-Z]{0,50}$")) {
    				log("DELETE!");
    				destFiles[i].delete();
    			}
    		}
    		
    		
    		// Write articles into destination dir!
            OutputStream out;    
            PrintWriter writer;

            for (int i = 0; i < articles.size(); i++) {

            	Record article = articles.get(i);
            	String filename = article.getString("filename");
            	
            	
                out = new FileOutputStream(new File(destDir + destDir.separator + filename));
                writer = new PrintWriter(out);
                
                String myHeader = header.replace("#KEYWORDS", article.getString("keywords"));
                myHeader = myHeader.replace("#DESCRIPTION", article.getString("title"));
                myHeader = myHeader.replace("#TITLE", article.getString("title"));
                
                writer.write(myHeader + "\r\n");
                
                
                // Compose a menu for each and every article.
        		// This is because the current article must get focus in the menu.
        		
        		StringBuffer menu = new StringBuffer();
        		String autofocus = "";
        		
        		for (int iMenu = articles.size()-1; iMenu >= 0; iMenu--) {
        			
        			if (iMenu == i) autofocus = "autofocus"; else autofocus = "";
        			
        			Record a = articles.get(iMenu);
        			menu.append("<a class=menuitem target='_self' href='" + a.getString("filename") + "'>\r\n");
        			menu.append("<button " + autofocus + ">\r\n");
        			menu.append("<span class=title>" + a.getString("title") + "</span>\r\n");
        			menu.append("<span class=date>" + a.getString("date") + "</span>\r\n");
        			menu.append("<span class=breaker></span>\r\n");
        			menu.append("<span class=keywords>" + a.getString("keywords") + "</span>\r\n");
        			menu.append("<span class=author>" + a.getString("author") + "</span>\r\n");
        			menu.append("</button>\r\n");
        			menu.append("</a>\r\n\r\n");
        		}
        		
        		// Write menu buffer into article.
        		log("MENU:\r\n" + menu.toString());
                writer.write(menu.toString() + "\r\n");
                writer.write(separator + "\r\n");
                
                
                // Decorate the title using data from metadata.
                writer.write("<h4>\r\n");
                writer.write("<span class=title>" + article.getString("title") + "</span>\r\n");
                writer.write("<span class=date>" + article.getString("date") + "</span>\r\n");
                writer.write("<span class=keywords>" + article.getString("keywords") + "</span>\r\n");
                writer.write("<span class=author>" + article.getString("author") + "</span>\r\n");
                writer.write("</h4>\r\n");
                
                writer.write(article.getString("content") + "\r\n");
                writer.write(footer + "\r\n");
                
                writer.close();
            }

            
            
            
            
            
        	
    		// Now compose a menu, newest first.
    		StringBuffer iMenu = new StringBuffer();
    		
    		for (int i = articles.size()-1; i >= articles.size() - 7; i--) {
    			
    			Record a = articles.get(i);
    			iMenu.append("<a class=menuitem target='_self' href='" + a.getString("filename") + "'>\r\n");
    			iMenu.append("<span class=title>" + a.getString("title") + "</span>\r\n");
    			iMenu.append("<span class=date>" + a.getString("date") + "</span>\r\n");
    			iMenu.append("<span class=breaker></span>\r\n");
    			iMenu.append("<span class=keywords>" + a.getString("keywords") + "</span>\r\n");
    			iMenu.append("<span class=author>" + a.getString("author") + "</span>\r\n");
    			iMenu.append("</a>\r\n\r\n");
    		}
    		
    		log("INDEX:\r\n" + iMenu.toString());

    		
    		
    		
            
        	// Write an index page, which resembles a menu.
        	// Let it present the site itself and 6 latest articles maybe.
            OutputStream iOut;    
            PrintWriter iWriter;

            String iHeader = load(new File(themeDir + themeDir.separator + "index-header.txt"));
            String iContent = load(new File(themeDir + themeDir.separator + "index-content.txt"));
            
            iOut = new FileOutputStream(new File(destDir + destDir.separator + "index.html"));
            iWriter = new PrintWriter(iOut);

            String myHeader = iHeader;
            iWriter.write(myHeader + "\r\n");
            
            
            // Compose a menu for the index.
    		
    		StringBuffer menu = new StringBuffer();
    		String autofocus = "";
    		
    		for (int idxMenu = articles.size()-1; idxMenu >= 0; idxMenu--) {
    			
    			Record a = articles.get(idxMenu);
    			menu.append("<a class=menuitem target='_self' href='" + a.getString("filename") + "'>\r\n");
    			menu.append("<button>\r\n");
    			menu.append("<span class=title>" + a.getString("title") + "</span>\r\n");
    			menu.append("<span class=date>" + a.getString("date") + "</span>\r\n");
    			menu.append("<span class=breaker></span>\r\n");
    			menu.append("<span class=keywords>" + a.getString("keywords") + "</span>\r\n");
    			menu.append("<span class=author>" + a.getString("author") + "</span>\r\n");
    			menu.append("</button>\r\n");
    			menu.append("</a>\r\n\r\n");
    		}
    		
    		// Write menu buffer into article.
    		log("INDEX MENU:\r\n" + menu.toString());
            iWriter.write(menu.toString() + "\r\n");

            
            
            iWriter.write(separator + "\r\n");
            
            // Write index page content
            iWriter.write(iContent);
            
            iWriter.write(footer + "\r\n");
            iWriter.close();
            
    	
	} catch (Exception e) {

		e.printStackTrace();
		System.exit(1);
	}




    	
        
    }
        
}
