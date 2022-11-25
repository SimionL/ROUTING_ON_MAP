package test;

import java.util.ArrayList;
import java.util.List;
import java.lang.Comparable;

public class Node implements Comparable{

	private List<Node> childrens = new ArrayList<>();;
	private String cca3;
	private boolean visited;

	public String getCca3() {
		return cca3;
	}

	public void setCca3(String cca3) {
		this.cca3 = cca3;
	}

	public List<Node> getChildrens() {
		return childrens;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
