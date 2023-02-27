/*
 TUIO Java GUI Demo
 Copyright (c) 2005-2016 Martin Kaltenbrunner <martin@tuio.org>
 
 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files
 (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge,
 publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:
 
 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import TUIO.*;


public class TuioDemoComponent extends JComponent implements TuioListener {

	private Hashtable<Long,TuioObject> objectList = new Hashtable<Long,TuioObject>();
	private Hashtable<Long,TuioCursor> cursorList = new Hashtable<Long,TuioCursor>();
	private Hashtable<Long,TuioBlob>   blobList   = new Hashtable<Long,TuioBlob>();

	public static final int finger_size = 18;
	public static final int object_size = 60;
	public static final int table_size = 760;
	
	public static int width, height;
	private float scale = 1.0f;
	public boolean verbose = false;
	
	Color bgrColor = new Color(54,54,54);
	Color curColor = new Color(192,0,192);
	Color objColor = new Color(64,0,0);
	Color blbColor = new Color(64,64,64);
		
	public void setSize(int w, int h) {
		super.setSize(w,h);
		width = w;
		height = h;
		scale  = height/(float)TuioDemoComponent.table_size;	
	}
	
	public void addTuioObject(TuioObject tobj) {
		objectList.put(tobj.getSessionID(),tobj);


		System.out.println("Seite "+tobj.getSymbolID()+" aufrufen.");


		if (verbose) 
			System.out.println("add obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());	
	}

	public void updateTuioObject(TuioObject tobj) {
		
		if (verbose) 
			System.out.println("set obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()+" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel()); 	
	}
	
	public void removeTuioObject(TuioObject tobj) {
		objectList.remove(tobj.getSessionID());
		
		if (verbose) 
			System.out.println("del obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+")");	
	}

	public void addTuioCursor(TuioCursor tcur) {
	
		cursorList.put(tcur.getSessionID(), tcur);

		if (verbose) 
			System.out.println("add cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+") "+tcur.getX()+" "+tcur.getY());	
	}

	public void updateTuioCursor(TuioCursor tcur) {
		
		if (verbose) 
			System.out.println("set cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+") "+tcur.getX()+" "+tcur.getY()+" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel()); 
	}
	
	public void removeTuioCursor(TuioCursor tcur) {
	
		cursorList.remove(tcur.getSessionID());	
		
		if (verbose) 
			System.out.println("del cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+")"); 
	}

	public void addTuioBlob(TuioBlob tblb) {
		blobList.put(tblb.getSessionID(),tblb);
		
		if (verbose) 
			System.out.println("add blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle());	
	}
	
	public void updateTuioBlob(TuioBlob tblb) {
		
		if (verbose) 
			System.out.println("set blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+") "+tblb.getX()+" "+tblb.getY()+" "+tblb.getAngle()+" "+tblb.getMotionSpeed()+" "+tblb.getRotationSpeed()+" "+tblb.getMotionAccel()+" "+tblb.getRotationAccel()); 	
	}
	
	public void removeTuioBlob(TuioBlob tblb) {
		blobList.remove(tblb.getSessionID());
		
		if (verbose) 
			System.out.println("del blb "+tblb.getBlobID()+" ("+tblb.getSessionID()+")");	
	}
	
	
	public void refresh(TuioTime frameTime) {
		repaint();
	}
	
	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
	
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


		g2.setColor(bgrColor);
		g2.fillRect(0,0,width,height);
		g2.setColor(Color.white);
		g2.drawString("KollegVision Presenter (ver. 0.1)",93,42);
		g2.drawString("©2023 - Benjamin Wagner",93,62);

		String logo = "/images/tk.png";
		URL logoUrl = getClass().getResource(logo);
		Image image = getToolkit().getImage(logoUrl);
		g2.drawImage(image,18,25, this);


		int w = (int)Math.round(width-scale*finger_size/2.0f);
		int h = (int)Math.round(height-scale*finger_size/2.0f);
		
		Enumeration<TuioCursor> cursors = cursorList.elements();
		while (cursors.hasMoreElements()) {
			TuioCursor tcur = cursors.nextElement();
			if (tcur==null) continue;
			ArrayList<TuioPoint> path = tcur.getPath();
			TuioPoint current_point = path.get(0);
			if (current_point!=null) {
				// draw the cursor path
				g2.setPaint(Color.blue);
				for (int i=0;i<path.size();i++) {
					TuioPoint next_point = path.get(i);
					g2.drawLine(current_point.getScreenX(w), current_point.getScreenY(h), next_point.getScreenX(w), next_point.getScreenY(h));
					current_point = next_point;
				}
			}
			
			// draw the finger tip
			g2.setPaint(curColor);
			int s = (int)(scale*finger_size);
			g2.fillOval(current_point.getScreenX(w-s/2),current_point.getScreenY(h-s/2),s,s);
			g2.setPaint(Color.white);
			g2.drawString(tcur.getCursorID()+"",current_point.getScreenX(w),current_point.getScreenY(h));
		}

		// draw the objects
		Enumeration<TuioObject> objects = objectList.elements();
		while (objects.hasMoreElements()) {
			TuioObject tobj = objects.nextElement();
			if (tobj!=null) {
			
				float ox = tobj.getScreenX(width);
				float oy = tobj.getScreenY(height);	
				float size = object_size*(height/(float)table_size);
						
				Rectangle2D square = new Rectangle2D.Float(-size/2,-size/2,size,size);

				AffineTransform transform = new AffineTransform();
				transform.rotate(tobj.getAngle(),ox,oy);
				transform.translate(ox,oy);

				JLabel text = new JLabel(String.valueOf(tobj.getSymbolID())+" ID");


				try {
					String destination = "https://www.vapita.de/uploads/"+tobj.getSymbolID()+".jpg";
					URL url = new URL(destination);
					BufferedImage c = ImageIO.read(url);
					g2.drawImage(c,Math.round(tobj.getX()*getWidth())-(c.getWidth()/2),Math.round(tobj.getY()*getHeight())-(c.getHeight()/2), this);
				} catch (IOException e) {
					System.err.println("Bild existiert nicht.");
				}

				//g2.setPaint(objColor);
				//g2.fill(transform.createTransformedShape(square));
				//g2.setPaint(objColor);
				//g2.drawString("Seite "+tobj.getSymbolID()+"",ox-10,oy);

			}
		}
		
		// draw the blobs
		Enumeration<TuioBlob> blobs = blobList.elements();
		while (blobs.hasMoreElements()) {
			TuioBlob tblb = blobs.nextElement();
			if (tblb!=null) {
			
				float bx = tblb.getScreenX(width);
				float by = tblb.getScreenY(height);
				float bw = tblb.getScreenWidth(width);
				float bh = tblb.getScreenHeight(height);
				Ellipse2D ellipse = new Ellipse2D.Float(-bw/2.0f,-bh/2.0f,bw,bh);
		
				AffineTransform transform = new AffineTransform();
				transform.rotate(tblb.getAngle(),bx,by);
				transform.translate(bx,by);

				g2.setPaint(blbColor);
				g2.fill(transform.createTransformedShape(ellipse));
				g2.setPaint(Color.white);
				g2.drawString(tblb.getBlobID()+"",bx-10,by);
			}
		}
	}
}
