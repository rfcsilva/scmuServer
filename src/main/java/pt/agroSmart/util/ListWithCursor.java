package pt.agroSmart.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListWithCursor implements Serializable {

	private static final long serialVersionUID = 1L;
	public boolean isFinished;
	public String cursor;
	@SuppressWarnings("rawtypes")
	public List list;
	
	@SuppressWarnings("rawtypes")
	public ListWithCursor(boolean isFinished, String cursor, List list ) {
		
		this.isFinished = isFinished;
		this.cursor = cursor;
		this.list = list;
		
	}

	public ListWithCursor(boolean isFinished) {
		
		cursor = "";
		list = new ArrayList<>(0);
		this.isFinished = isFinished;
	}
	
}
