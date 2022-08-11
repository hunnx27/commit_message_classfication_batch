package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class main {

	public static void main(String[] args) throws IOException {
//		File left = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\com\\android\\systemui\\accessibility\\AccessibilityButtonModeObserver.java");
//		File right = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\com\\android\\systemui\\accessibility\\AccessibilityButtonModeObserver2.java");
//		File left = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\com\\android\\systemui\\accessibility\\classA.java");
//		File right = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\com\\android\\systemui\\accessibility\\classB.java");
//		File left = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\classA.java");
//		File right = new File("D:\\develop2\\workspace_ai\\platform_frameworks_base\\packages\\SystemUI\\src\\classB.java");
//		File left = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\AccessibilityButtonModeObserver.java"); // TODO 되는 예제
//		File right = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\AccessibilityButtonModeObserver2.java"); // TODO 되는 예제
//		File left = new File("D:\\develop2\\\\workspace_ai\\elasticsearch\\AccessibilityButtonModeObserver.java"); // TODO 되는 예제
//		File right = new File("D:\\develop2\\\\workspace_ai\\elasticsearch\\AccessibilityButtonModeObserver2.java"); // TODO 되는 예제
//		File left = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\classA.java");
//		File right = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\classB.java");		
//		File left = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\CommentInsert_Left.java");
//		File right = new File("D:\\develop\\git\\oneandzip_bo\\src\\main\\java\\CommentInsert_Right.java");
//		File left = new File("D:\\develop2\\workspace_ai\\changedistiller\\resources\\testdata\\src_changetypes\\CommentInsert_Left.java");
//		File right = new File("D:\\develop2\\workspace_ai\\changedistiller\\resources\\testdata\\src_changetypes\\CommentInsert_Right.java");
		//		File right = new File("D:\\develop2\\workspace_ai\\Right.java");
		File left = new File("D:\\develop2\\workspace_ai\\elasticsearch\\file1.java"); // TODO 되는 예제
		File right = new File("D:\\develop2\\workspace_ai\\elasticsearch\\file2.java"); // TODO 되는 예제

		
		
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right); 
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.err.println("Warning: error while change distilling. " + e.getMessage());
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		System.out.println(changes.size());
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		        // see Javadocs for more information
		    	System.out.println(change.getChangeType().ordinal() + " : " + change.getChangeType() + " : " + change.toString());
		    }
		}

	}
	
	static void printFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String str;
		while ((str = reader.readLine()) != null) {
			System.out.println(str);
		}
		reader.close();		
	}

}
