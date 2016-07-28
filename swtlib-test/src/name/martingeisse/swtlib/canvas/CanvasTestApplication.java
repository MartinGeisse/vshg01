/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.canvas;

import java.nio.ByteBuffer;

import name.martingeisse.swtlib.color.Colors;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.opengl.GL11;

/**
 * Test for the test wizard.
 */
public class CanvasTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public CanvasTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new ContentRetainingCanvasTest(),
			new BlockContentRetainingCanvasTest(),
			new OpenGlBlockCanvasTest(),
			new OpenGlBitmapBlockCanvasTest(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		CanvasTestApplication app = new CanvasTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	private class ContentRetainingCanvasTest implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			ContentRetainingCanvas canvas = new ContentRetainingCanvas(parent, 200, 200);
			GC gc = new GC(canvas.getContents());
			gc.setBackground(Colors.getLightRed());
			gc.fillRectangle(0, 0, 200, 200);
			gc.setBackground(Colors.getLightBlue());
			gc.fillRectangle(30, 30, 140, 140);
			gc.setBackground(Colors.getLightGreen());
			gc.fillRectangle(60, 60, 80, 80);
			gc.dispose();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The component should show nested colored rectangles that don't move on resize and redraw correctly when obscured.";
		}

	}

	private class BlockContentRetainingCanvasTest implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			new AbstractBlockContentRetainingCanvas(parent, 8, 16, 40, 20) {
				
				@Override
				protected void drawBlock(ImageData data, int blockIndexX, int blockIndexY) {
					int color;
					if (((blockIndexX ^ blockIndexY) & 1) == 0) {
						color = 0x00ff0000;
					} else if ((blockIndexY & 1) == 0) {
						color = 0x0000ff00;
					} else {
						color = 0x000000ff;
					}
					for (int x=0; x<8; x++) {
						for (int y=0; y<16; y++) {
							data.setPixel(x + blockIndexX * 8, y + blockIndexY * 16, color);
						}
					}
				}
			}.updateAllBlocks();
			
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The component should show colored 8x16 blocks (R-G, B-R) with a fixed canvas size that don't move on resize and redraw correctly when obscured.";
		}

	}

	private class OpenGlBlockCanvasTest implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			new AbstractOpenGlBlockCanvas(parent, 8, 16, 40, 20) {
				
				@Override
				protected void drawBlock(int blockIndexX, int blockIndexY) {
					if (((blockIndexX ^ blockIndexY) & 1) == 0) {
						GL11.glColor3f(1.0f, 0.0f, 0.0f);
					} else if ((blockIndexY & 1) == 0) {
						GL11.glColor3f(0.0f, 1.0f, 0.0f);
					} else {
						GL11.glColor3f(0.0f, 0.0f, 1.0f);
					}
					
					float startX = blockIndexX;
					float startY = blockIndexY;
					float endX = startX + 1.0f;
					float endY = startY + 1.0f;
					
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2f(startX, startY);
					GL11.glVertex2f(startX, endY);
					GL11.glVertex2f(endX, endY);
					GL11.glVertex2f(endX, startY);
					GL11.glEnd();

				}
			};
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The component should show colored 8x16 blocks (R-G, B-R) with a fixed canvas size that don't move on resize and redraw correctly when obscured.";
		}

	}

	private class OpenGlBitmapBlockCanvasTest implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			
			final ByteBuffer bitmapBuffer = ByteBuffer.allocateDirect(64);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(16+8));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(64+2));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(128+1));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(64+2));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(16+8));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(16+8));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(64+2));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(128+1));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(64+2));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(16+8));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)(32+4));
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);
			bitmapBuffer.put((byte)0);

			new AbstractOpenGlBlockCanvas(parent, 8, 16, 40, 20) {
				
				@Override
				protected void drawBlock(int blockIndexX, int blockIndexY) {
					if (((blockIndexX ^ blockIndexY) & 1) == 0) {
						GL11.glColor3f(1.0f, 0.0f, 0.0f);
					} else if ((blockIndexY & 1) == 0) {
						GL11.glColor3f(0.0f, 1.0f, 0.0f);
					} else {
						GL11.glColor3f(0.0f, 0.0f, 1.0f);
					}
					
					float startX = blockIndexX;
					float startY = blockIndexY;
					
					bitmapBuffer.rewind();
					GL11.glRasterPos2f(startX, startY + 1);
					GL11.glBitmap(8, 16, 0.0f, 0.0f, 0.0f, 0.0f, bitmapBuffer);
					
				}
			};
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The component should show colored 8x16 blocks (R-G, B-R) with a fixed canvas size that don't move on resize and redraw correctly when obscured.";
		}

	}

}
