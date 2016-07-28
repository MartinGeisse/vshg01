/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.canvas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

/**
 * This canvas draws its contents from a block-oriented data source.
 */
public abstract class AbstractOpenGlBlockCanvas extends GLCanvas {

	/**
	 * the blockWidth
	 */
	private int blockWidth;

	/**
	 * the blockHeight
	 */
	private int blockHeight;

	/**
	 * the horizontalBlockCount
	 */
	private int horizontalBlockCount;

	/**
	 * the verticalBlockCount
	 */
	private int verticalBlockCount;

	/**
	 * the delayUpdates
	 */
	private boolean delayUpdates;

	/**
	 * the matrixByteBuffer
	 */
	private ByteBuffer matrixByteBuffer;

	/**
	 * the matrixBuffer
	 */
	private FloatBuffer matrixBuffer;

	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param blockWidth the width of a single block
	 * @param blockHeight the height of a single block
	 * @param horizontalBlockCount the number of blocks in a horizontal row
	 * @param verticalBlockCount the number of blocks in a vertical column
	 */
	public AbstractOpenGlBlockCanvas(Composite parent, int blockWidth, int blockHeight, int horizontalBlockCount, int verticalBlockCount) {
		super(parent, 0, createGlData());
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.horizontalBlockCount = horizontalBlockCount;
		this.verticalBlockCount = verticalBlockCount;
		this.delayUpdates = false;
		
		this.matrixByteBuffer = ByteBuffer.allocateDirect(16 * 4);
		matrixByteBuffer.order(ByteOrder.nativeOrder());
		matrixByteBuffer.clear();
		this.matrixBuffer = matrixByteBuffer.asFloatBuffer();
		matrixBuffer.clear();

		matrixBuffer.put(2.0f / horizontalBlockCount);
		matrixBuffer.put(0.0f);
		matrixBuffer.put(0.0f);
		matrixBuffer.put(0.0f);

		matrixBuffer.put(0.0f);
		matrixBuffer.put(-2.0f / verticalBlockCount);
		matrixBuffer.put(0.0f);
		matrixBuffer.put(0.0f);

		matrixBuffer.put(0.0f);
		matrixBuffer.put(0.0f);
		matrixBuffer.put(1.0f);
		matrixBuffer.put(0.0f);

		matrixBuffer.put(-1.0f);
		matrixBuffer.put(1.0f);
		matrixBuffer.put(0.0f);
		matrixBuffer.put(1.0f);

		setupOpenGl();
		addPaintListener(new MyPaintListener());

	}
	
	/**
	 * Creates a {@link GLData} instance for this canvas.
	 * @return Returns the GL data.
	 */
	private static GLData createGlData() {
		GLData data = new GLData();
		data.doubleBuffer = true;
		return data;
	}

	/**
	 * @return Returns the blockWidth.
	 */
	public int getBlockWidth() {
		return blockWidth;
	}

	/**
	 * @return Returns the blockHeight.
	 */
	public int getBlockHeight() {
		return blockHeight;
	}

	/**
	 * @return Returns the horizontalBlockCount.
	 */
	public int getHorizontalBlockCount() {
		return horizontalBlockCount;
	}

	/**
	 * @return Returns the verticalBlockCount.
	 */
	public int getVerticalBlockCount() {
		return verticalBlockCount;
	}

	/**
	 * @return Returns the delayUpdates.
	 */
	public boolean isDelayUpdates() {
		return delayUpdates;
	}

	/**
	 * Sets the delayUpdates.
	 * @param delayUpdates the new value to set
	 */
	public void setDelayUpdates(boolean delayUpdates) {
		boolean updateNow = this.delayUpdates && !delayUpdates;
		this.delayUpdates = delayUpdates;
		if (updateNow) {
			updateAllBlocks();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(blockWidth * horizontalBlockCount, blockHeight * verticalBlockCount);
	}

	/**
	 * This method is invoked once before one or more call(s) to drawBlock().
	 */
	protected void onBeforeDraw() {
	}
	
	/**
	 * This method must be implemented by subclassed to draw a block in the contents.
	 * @param blockIndexX the X index of the block to draw
	 * @param blockIndexY the Y index of the block to draw
	 */
	protected abstract void drawBlock(int blockIndexX, int blockIndexY);

	/**
	 * This method is invoked once after one or more call(s) to drawBlock().
	 */
	protected void onAfterDraw() {
	}
	
	/**
	 * Causes this canvas to refresh the specified block from the underlying data source
	 * and redraw it.
	 * @param blockIndexX the X index of the block to update
	 * @param blockIndexY the Y index of the block to update
	 */
	public void updateBlock(int blockIndexX, int blockIndexY) {
		if (!delayUpdates) {
			scheduleRedraw();
		}
	}

	/**
	 * Causes this canvas to refresh all blocks from the underlying data source and
	 * redraw them.
	 */
	public void updateAllBlocks() {
		if (!delayUpdates) {
			scheduleRedraw();
		}
	}
	
	/**
	 * 
	 */
	private void setupOpenGl() {
		prepareOpenGl();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		matrixBuffer.rewind();
		GL11.glLoadMatrix(matrixBuffer);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
	}
	
	/**
	 * 
	 */
	private void prepareOpenGl() {
		setCurrent();
		try {
			GLContext.useContext(this);
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
		GL11.glViewport(0, 0, blockWidth * horizontalBlockCount, blockHeight * verticalBlockCount);
	}
	
	/**
	 * 
	 */
	private void finishOpenGl() {
		swapBuffers();
	}

	private void scheduleRedraw() {
			/**
			 * Drawing is done asynchronously. At least on
			 * Linux, if the paint listener did the drawing
			 * work directly, then drawn content is not visible.
			 * I suspect that the canvas retains its contents, but
			 * OpenGL bypasses this buffer and draws directly to the
			 * screen. As soon as the paint listener is finished,
			 * the canvas copies its buffer to the screen, overwriting
			 * the result of the OpenGL drawing operations.
			 */
			getDisplay().asyncExec(new Runnable() {
			
				@Override
				public void run() {

					/** set up OpenGL **/
					prepareOpenGl();

					/** draw the blocks **/
					onBeforeDraw();
					for (int i=0; i<horizontalBlockCount; i++) {
						for (int j=0; j<verticalBlockCount; j++) {
							drawBlock(i, j);
						}
					}
					onAfterDraw();
			
					/** finish openGL operations **/
					finishOpenGl();

				}

			});
	}
	
	/**
	 * This paint listener draws the contents of the canvas on the GC.
	 */
	private class MyPaintListener implements PaintListener {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(PaintEvent event) {
			scheduleRedraw();		
		}

	}

}
