package com.dickie.sidion.client;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Image;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.dickie.sidion.shared.GameComponent;
import com.dickie.sidion.shared.Town;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;


public class MapPanel extends DecoratorPanel {
	DrawingArea canvas = new DrawingArea(800, 800);

	public boolean initialized = false; 
	
	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	
	DrawingArea getCanvas(){
		return canvas;
	}

	public MapPanel() {
		Image image = new Image(0, 0, 800, 800, "Project1.png");
		canvas.add(image);
		this.add(canvas);
	}
	
	public void drawMapBackground(){
		initialized = true;
		Image image = new Image(0, 0, 800, 800, "Project1.png");
		canvas.add(image);
	}
	
	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public String getLineColor() {
		return lineColor;
	}

	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}

	String fillColor = "#000000";
	String lineColor = "#FFFFFF";
	

	public void drawRec(int x, int y, int size, String label, final GameComponent gc){
		int textDrop = size/4;
		Rectangle rec = new Rectangle(x - size/2, y-size/2, size, size);
		rec.setStrokeColor(lineColor);
		rec.setFillColor(fillColor);
		canvas.add(rec);
		rec.addClickHandler(new ClickHandler() {
			  public void onClick(ClickEvent event) {
			    gc.selected();
			  }
			});
		addLabel(x,y,size,label);
	}
	
	public void drawCircle(int x, int y, int size, String label,final GameComponent gc){
		
		Circle c = new Circle(x, y, size);
		c.setStrokeColor(lineColor);
		c.setFillColor(fillColor);
		canvas.add(c);
		c.addClickHandler(new ClickHandler() {
			  public void onClick(ClickEvent event) {
				    gc.selected();
				  }
				});
		addLabel(x,y,size,label);
	}
	
	public void drawPath(int x1, int y1, int x2, int y2, final GameComponent gc){
		Line line = new Line(x1, y1, x2, y2);
		drawRec(x1 + java.lang.Math.abs(x1 - x2)/2, y1 + java.lang.Math.abs(y1 - y2)/2, 8, "", gc);
	}
	
	private void addLabel(int x, int y, int size, String label){
		if (label == null){
			return;
		}
		int textDrop = size/4;
		Text t = new Text(x, y + size/2,label);
		t.setFontSize(12);
		int len = t.getTextWidth();
		t.setX(t.getX() - len/2);
		t.setY(t.getY() + (int)(t.getTextHeight()));
		canvas.add(t);
	}

	
	
	public void clearCanvas(){
		canvas.clear();
	}

}

