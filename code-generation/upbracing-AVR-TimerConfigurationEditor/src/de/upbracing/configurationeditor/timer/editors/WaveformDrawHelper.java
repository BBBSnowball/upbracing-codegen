package de.upbracing.configurationeditor.timer.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import de.upbracing.configurationeditor.timer.Activator;
import de.upbracing.configurationeditor.timer.viewmodel.UseCaseViewModel;
import de.upbracing.shared.timer.model.enums.CTCOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;
import de.upbracing.shared.timer.model.validation.UseCaseModelValidator;

/**
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 *
 */
public class WaveformDrawHelper {
	
	// Width of one slope
	private static int waveCycleWidth = 60;
	private static int waveHeight = 80;
	// Horizontal and vertical offset of waveform
	private static int yWaveOffset = 45;
	private static int xWaveOffset = 115;
	// xOffset for text:
	private static int xTextOffset = 5;
	// yOffset for period marker:
	private static int yPeriodMarkerOffset = 10;
	private static int yPeriodMarkerHeight = 10;
	// Size for interrupt bullets:
	private static int interruptBulletSize = 7;
	// Vertical padding for output channels:
	private static int yOutputPinPadding = 20;
	
	// Used colors
	private static Color COLOR_BLACK
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
	private static Color COLOR_CHANNEL_A 
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN);
	private static Color COLOR_CHANNEL_B 
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	private static Color COLOR_CHANNEL_C 
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
	
	public static void drawWaveform(GC gc, boolean dualSlope) {
		gc.setLineWidth(2);
		if (!dualSlope) {
			for (int i = 0; i < 4; i++) {
			    gc.drawPolyline(new int[] { 
	    		xWaveOffset + waveCycleWidth * i,     yWaveOffset + waveHeight, 
	    		xWaveOffset + waveCycleWidth * (i+1), yWaveOffset,
	    		xWaveOffset + waveCycleWidth * (i+1), yWaveOffset + waveHeight });
			}
		} else {
			gc.drawPolyline(new int[] { 
					xWaveOffset,                  	  yWaveOffset + waveHeight, 
		    		xWaveOffset + waveCycleWidth,     yWaveOffset, 
		    		xWaveOffset + waveCycleWidth * 2, yWaveOffset + waveHeight, 
		    		xWaveOffset + waveCycleWidth * 3, yWaveOffset, 
		    		xWaveOffset + waveCycleWidth * 4, yWaveOffset + waveHeight });
		}
	}
	
	public static void drawWaveformChannels(GC gc, UseCaseViewModel model) {
		
		drawTopLine(gc, model);
		
		// 1) Get Top value:
		double topPeriod = model.getValidator().calculateQuantizedPeriod(model.getValidator().getTopPeriod());
		// 2) Put all periods in hashmap
		HashMap<Double, String> hm = getRelevantChannels(model);
		// Draw the sorted list
		TreeSet<Double> periods = new TreeSet<Double>(hm.keySet());
		int count = periods.size();
		int counter = 1;
		for (Double period : periods) {
			String value = hm.get(period);
			Point p;
			boolean compareInterrupt = false;
			Color color = null;
			if (value.substring(7).contains("A")) {
				compareInterrupt = model.getCompareInterruptA();
				color = COLOR_CHANNEL_A;
			}
			if (value.substring(7).contains("B")) {
				compareInterrupt |= model.getCompareInterruptB();
				if (color == null)
					color = COLOR_CHANNEL_B;
			}
			if (value.substring(7).contains("C")) {
				compareInterrupt |= model.getCompareInterruptC();
				if (color == null)
					color = COLOR_CHANNEL_C;
			}
			if (count > 1) {
				// Line:
				WaveformDrawHelper.drawHorizontalLine(gc, color, (100 / (count + 1)) * counter, (int) ((period / topPeriod) * 100), value);
				counter++;
			} else {
				WaveformDrawHelper.drawHorizontalLine(gc, color, 50, (int) ((period / topPeriod) * 100), value);	
			}
			if (model.getMode().equals(TimerOperationModes.CTC)) {
				// Interrupt bullets:
				p = getSectionPoint(period, topPeriod, model.getValidator());
				drawChannelInterrupts(gc, color, p, compareInterrupt);
			}
		}
		
		// Draw period markers for output pins:
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_DASH);
		gc.setAntialias(SWT.OFF);
		
		if (!(model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT)
				|| model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT))) {
			// Single Slope:
			for (int i = 0; i < 5; i++) {
				gc.drawLine(xWaveOffset + waveCycleWidth * i, yWaveOffset - 5, 
						    xWaveOffset + waveCycleWidth * i, yWaveOffset + waveHeight + yOutputPinPadding + 66);
			}
		} else {
			// Dual Slope:
			for (int i = 0; i < 3; i++) {
				gc.drawLine(xWaveOffset + waveCycleWidth * i * 2, yWaveOffset - 5, 
					    	xWaveOffset + waveCycleWidth * i * 2, yWaveOffset + waveHeight + yOutputPinPadding + 66);
			}
		}
		
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setAntialias(SWT.ON);
	}

	public static void drawHorizontalLine(GC gc, int startPercent, int endPercent, String txt) {
		drawHorizontalLine(gc, COLOR_BLACK, startPercent, endPercent, txt, 1, false);
	}
	
	public static void drawHorizontalLine(GC gc, int startPercent, int endPercent, String txt, int lineCount, boolean alignBottom) {
		drawHorizontalLine(gc, COLOR_BLACK, startPercent, endPercent, txt, lineCount, alignBottom);
	}
	
	public static void drawHorizontalLine(GC gc, Color c, int startPercent, int endPercent, String txt) {
		drawHorizontalLine(gc, c, startPercent, endPercent, txt, 1, false);
	}
	
	public static void drawHorizontalLine(GC gc, Color c, int startPercent, int endPercent, String txt, int linecount, boolean alignBottom) {
	    
		int x1 = xWaveOffset - 5;
		int x2 = xWaveOffset + 5 + (waveCycleWidth * 4);
		int y1 = getYCoordinateFromPercentage(startPercent);
		int y2 = getYCoordinateFromPercentage(endPercent);
		
		gc.setLineWidth(1);
		gc.setAntialias(SWT.OFF);
	    gc.setForeground(c);
	    
	    // Draw line:
	    gc.drawPolyline(new int[] { x1 - 30, y1, x1 - 10, y1, x1 - 10, y2, x2, y2});
	    	    
	    // Draw text:
	    int lineheight = gc.getFontMetrics().getHeight();
	    if (alignBottom) {
	    	gc.drawText(txt, xTextOffset, y1 - 7 - ((linecount - 1) * lineheight), true);
	    } else {
	    	gc.drawText(txt, xTextOffset, y1 - 7, true);
	    }
	    gc.setForeground(COLOR_BLACK);
	    gc.setAntialias(SWT.ON);
	}
	
	public static void drawPeriodText(GC gc, String txt, boolean dualSlope) {
		gc.setForeground(COLOR_BLACK);
		int stretchfactor = 2;
		if (dualSlope) {
			stretchfactor = 3;
		}
		
		int xLeft     = xWaveOffset + waveCycleWidth * 2;
		int xRight    = xWaveOffset + waveCycleWidth + waveCycleWidth * stretchfactor; 
		int yArrowMid = yWaveOffset - yPeriodMarkerOffset - (yPeriodMarkerHeight / 2);
		
		// Left vertical marker
		gc.drawLine(xLeft, yWaveOffset - yPeriodMarkerOffset - yPeriodMarkerHeight, 
				    xLeft, yWaveOffset - yPeriodMarkerOffset);
		// Right vertical marker
		gc.drawLine(xRight, yWaveOffset - yPeriodMarkerOffset - yPeriodMarkerHeight, 
				    xRight, yWaveOffset - yPeriodMarkerOffset);
		
		// Draw left arrow cap
		gc.drawPolyline(new int[] {
				xLeft + 7, yArrowMid - 4, 
				xLeft + 2, yArrowMid, 
				xLeft + 7, yArrowMid + 4});
		// Draw right arrow cap
		gc.drawPolyline(new int[] {
				xRight - 7, yArrowMid - 4, 
				xRight - 2, yArrowMid, 
				xRight - 7, yArrowMid + 4});
		
		// Draw line between arrow caps
		gc.drawLine(xLeft  + 3, yArrowMid, 
				    xRight - 3, yArrowMid);
		
		// Draw centered text
		Point overflowPeriodStringWidth = gc.stringExtent(txt);
		int xDiff = (waveCycleWidth - overflowPeriodStringWidth.x) / 2;
		gc.drawText(txt, 
				    xWaveOffset + waveCycleWidth + ((int)(waveCycleWidth * (stretchfactor / 2.0))) + xDiff, 
				    yWaveOffset - yPeriodMarkerOffset - yPeriodMarkerHeight - 15, 
				    true);
	}
	
	public static void drawResetInterrupts(GC gc, boolean enabled) {
		drawChannelInterrupts(gc, COLOR_CHANNEL_A, new Point(0, 0), enabled);
	}
	
	public static void drawOverflowInterruptText(GC gc, boolean enabled) {
		String overflowInterruptText = "Overflow interrupt ";
    	if (enabled) {
    		overflowInterruptText += "enabled";
    	} else {
    		overflowInterruptText += "disabled";
    	}
    	int xDiff = (3 * waveCycleWidth - gc.stringExtent(overflowInterruptText).x) / 2;
    	gc.drawText(overflowInterruptText, xWaveOffset + waveCycleWidth + xDiff, yWaveOffset + waveHeight + 10, true);
	}
	
	public static void drawTopLine(GC gc, UseCaseViewModel model) {
		List<String> lst = new ArrayList<String>();
		int topRegValue = model.getValidator().calculateRegisterValue(model.getValidator().getTopPeriod());
		if (model.getValidator().calculateRegisterValue(model.getIcrPeriod()) == topRegValue)
			lst.add("ICR");
		if (model.getValidator().calculateRegisterValue(model.getOcrAPeriod()) == topRegValue)
			lst.add("A");
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
			if (model.getValidator().calculateRegisterValue(model.getOcrBPeriod()) == topRegValue)
				lst.add("B");
			if (model.getValidator().calculateRegisterValue(model.getOcrCPeriod()) == topRegValue)
				lst.add("C");
		}
		
		String lineText = "TOP";
		WaveformDrawHelper.drawHorizontalLine(gc, 100, 100, lineText);
	}
	
	public static void drawCTCOutputPin(GC gc, 
										UseCaseViewModel model, 
										String register, 
										double period,
										CTCOutputPinMode mode) {
		int y = getYCoordinateForOutputPin(register);
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(period, model.getValidator().getTopPeriod(), model.getValidator());
		if (p != null) {
			if (register.equals("Channel A") ||
					model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (mode.equals(CTCOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, getColorForOutputPin(register), p.x, y, false);
				} else if (mode.equals(CTCOutputPinMode.CLEAR)) {
					drawOutputPinClearSetLine(gc, getColorForOutputPin(register), p.x, y, false);
				} else if (mode.equals(CTCOutputPinMode.SET)) {
					drawOutputPinClearSetLine(gc, getColorForOutputPin(register), p.x, y, true);
				} else {
					gc.drawString(mode.toString(), xWaveOffset + 5, y, true);
				}
			} else {
				gc.drawString("N/A", xWaveOffset + 5, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xWaveOffset + 5, y, true);
		}
	}
	
	public static void drawSingleSlopePWMOutputPin(GC gc, 
												   UseCaseViewModel model, 
												   String register, 
												   double period,
												   PWMSingleSlopeOutputPinMode mode) {
		int y = getYCoordinateForOutputPin(register);
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(period, model.getValidator().getTopPeriod(), model.getValidator());
		if (p != null) {
			if (register.equals("Channel A") ||
					model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (mode.equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, getColorForOutputPin(register), p.x, y, false);
				} else if (mode.equals(PWMSingleSlopeOutputPinMode.CLEAR)) {
					drawOutputPinPWMLine(gc, getColorForOutputPin(register), p.x, y, false, false);
				} else if (mode.equals(PWMSingleSlopeOutputPinMode.SET)) {
					drawOutputPinPWMLine(gc, getColorForOutputPin(register), p.x, y, false, true);
				} else {
					gc.drawString(mode.toString(), xWaveOffset + 5, y, true);
				}
			} else {
				gc.drawString("N/A", xWaveOffset + 5, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xWaveOffset + 5, y, true);
		}
	}
	
	public static void drawDualSlopePWMOutputPin(GC gc, 
												 UseCaseViewModel model, 
												 String register, 
												 double period,
												 PWMDualSlopeOutputPinMode mode) {
		int y = getYCoordinateForOutputPin(register);
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(period, model.getValidator().getTopPeriod(), model.getValidator());
		if (p != null) {
			if (register.equals("Channel A") ||
					model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (mode.equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, getColorForOutputPin(register), p.x, y, true);
				} else if (mode.equals(PWMDualSlopeOutputPinMode.CLEAR_SET)) {
					drawOutputPinPWMLine(gc, getColorForOutputPin(register), p.x, y, true, false);
				} else if (mode.equals(PWMDualSlopeOutputPinMode.SET_CLEAR)) {
					drawOutputPinPWMLine(gc, getColorForOutputPin(register), p.x, y, true, true);
				} else {
					gc.drawString(mode.toString(), xWaveOffset + 5, y, true);
				}
			} else {
				gc.drawString("N/A", xWaveOffset + 5, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xWaveOffset + 5, y, true);
		}
	}
	
	private static void drawOutputPinToggleLine(GC gc, Color c, int x, int y, boolean dualSlope) {
		gc.setLineWidth(2);
		int xStart = xWaveOffset;
		int xOff = xWaveOffset + x + waveCycleWidth;
		int yLow = y + 14;
		int yHigh = y;
		gc.setForeground(c);
		if (x <= 0) {
			if (!dualSlope) {
				gc.drawPolyline(new int[] {
						xStart, yLow, 
						xOff, yLow, 
						xOff, yHigh, 
						xOff + waveCycleWidth, yHigh,
						xOff + waveCycleWidth, yLow, 
						xOff + waveCycleWidth * 2, yLow, 
						xOff + waveCycleWidth * 2, yHigh, 
						xOff + waveCycleWidth * 3, yHigh,
						xOff + waveCycleWidth * 3, yLow,
						xStart + waveCycleWidth * 4, yLow});
			} else {
				gc.drawPolyline(new int[] {
						xStart, yLow, 
						xOff, yLow,
						xOff, yHigh,
						xOff + waveCycleWidth * 2, yHigh, 
						xOff + waveCycleWidth * 2, yLow, 
						xStart + waveCycleWidth * 4, yLow});
			}
		} else {
			gc.drawLine(xStart, yLow, xStart + waveCycleWidth * 4, yLow);
		}
		gc.setForeground(COLOR_BLACK);
	}
	
	private static void drawOutputPinClearSetLine(GC gc, Color c, int x, int y, boolean set) {
		gc.setLineWidth(2);
		int xStart = xWaveOffset;
		int xOff = xWaveOffset + x + waveCycleWidth;
		int yLow = y + 14;
		int yHigh = y;
		gc.setForeground(c);
		int dontCareYOffset = 0;
		
		// Draw arrows:
		if (x <= 0) {
			for (int i = 0; i < 4; i++) {
				if (set) {
					gc.drawPolyline(new int[] {
							xOff + waveCycleWidth * i - 4, yHigh + 4, 
							xOff + waveCycleWidth * i, yHigh, 
							xOff + waveCycleWidth * i + 4, yHigh + 4
						});
					dontCareYOffset = 2;
				} else {
					gc.drawPolyline(new int[] {
							xOff + waveCycleWidth * i - 4, yLow - 4, 
							xOff + waveCycleWidth * i, yLow, 
							xOff + waveCycleWidth * i + 4, yLow - 4
						});
					dontCareYOffset = -2;
				}
				gc.drawLine(xOff + waveCycleWidth * i, yLow, xOff + waveCycleWidth * i, yHigh);
			}
		} 
		
		// Draw don't care line:
		gc.setLineWidth(1);
		int crossWidth = 6;
		int numberOfCrosses = (4 * waveCycleWidth) / crossWidth;
		for (int i = 0; i < numberOfCrosses; i++) {
			gc.drawLine(xStart + crossWidth * i, yLow - 5 + dontCareYOffset, 
					xStart + crossWidth * i + crossWidth, yHigh + 5 + dontCareYOffset);
			gc.drawLine(xStart + crossWidth * i, yHigh + 5 + dontCareYOffset,
					xStart + crossWidth * i + crossWidth, yLow - 5 + dontCareYOffset);
		}
		
		gc.setForeground(COLOR_BLACK);
	}
	
	private static void drawOutputPinPWMLine(GC gc, Color c, int x, int y, boolean dualSlope, boolean inverted) {
		gc.setLineWidth(2);
		int xStart = xWaveOffset;
		int xOff = xWaveOffset + x + waveCycleWidth;
		int yLow = y;
		int yHigh = y + 14;
		if (inverted) {
			yLow = y + 14;
			yHigh = y;
		}
		gc.setForeground(c);
		if (x <= 0) {
			if (!dualSlope) {
				for (int i = 0; i < 4; i++) {
					gc.drawPolyline(new int[] {
						xStart + waveCycleWidth * i, yLow, 
						xOff + waveCycleWidth * i, yLow, 
						xOff + waveCycleWidth * i, yHigh, 
						xStart + waveCycleWidth * (i+1), yHigh,
						xStart + waveCycleWidth * (i+1), yLow,	
					});
				}
			} else {
				for (int i = 0; i < 2; i++) {
					gc.drawPolyline(new int[] {
						xStart + waveCycleWidth * i * 2, yLow, 
						xOff + waveCycleWidth * i * 2, yLow,
						xOff + waveCycleWidth * i * 2, yHigh,
						2 * xStart + waveCycleWidth * (i+1) * 2 - xOff, yHigh,
						2 * xStart + waveCycleWidth * (i+1) * 2 - xOff, yLow,
						xStart + waveCycleWidth * (i+1) * 2, yLow
					});
				}
			}
		} else {
			gc.drawLine(xStart, yLow, xStart + waveCycleWidth * 4, yLow);
		}
		gc.setForeground(COLOR_BLACK);
	}
	
	private static void drawChannelInterrupts(GC gc, Color c, Point p, boolean enabled) {
		
		if (enabled) {
			gc.setLineStyle(SWT.LINE_SOLID);
		    
		    int innerBulletOffset = interruptBulletSize / 2;
		    int y = getYCoordinateFromPercentage(100);
		    
		    // Bullet:
		    Color colBg = gc.getBackground();
		    gc.setBackground(c);
		    for (int i = 1; i < 5; i++) {
		    	gc.fillOval(p.x + xWaveOffset - innerBulletOffset + waveCycleWidth * i, p.y + y - innerBulletOffset, interruptBulletSize, interruptBulletSize);
		    }
		    gc.setBackground(colBg);
	    	gc.setForeground(COLOR_BLACK);
		}
	}
	
	private static HashMap<Double, String> getRelevantChannels(UseCaseViewModel model) {
		// 1) Get top period
		double topPeriod = model.getValidator().getTopPeriod();
		// 2) Put all periods in hashmap
		HashMap<Double, String> hm = new HashMap<Double, String>();
		hm.put(model.getValidator().calculateQuantizedPeriod(model.getOcrAPeriod()), "Channel A");
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
			if (hm.containsKey(model.getValidator().calculateQuantizedPeriod(model.getOcrBPeriod())))
				hm.put(model.getValidator().calculateQuantizedPeriod(model.getOcrBPeriod()), 
					   hm.get(model.getValidator().calculateQuantizedPeriod(model.getOcrBPeriod())) + ", B");
			else
				hm.put(model.getValidator().calculateQuantizedPeriod(model.getOcrBPeriod()), "Channel B");
			if (hm.containsKey(model.getValidator().calculateQuantizedPeriod(model.getOcrCPeriod())))
				hm.put(model.getValidator().calculateQuantizedPeriod(model.getOcrCPeriod()), 
					   hm.get(model.getValidator().calculateQuantizedPeriod(model.getOcrCPeriod())) + ", C");
			else
				hm.put(model.getValidator().calculateQuantizedPeriod(model.getOcrCPeriod()), "Channel C");
		}
		// 3) Cleanup sorted list
		TreeSet<Double> periods = new TreeSet<Double>(hm.keySet());
		for (Double period : periods) {
			if (period > model.getValidator().calculateQuantizedPeriod(topPeriod)) {
				hm.remove(period);
			}
		}
		return hm;
	}
	
	private static Point getSectionPoint(double period, double topPeriod, UseCaseModelValidator val) {
		
		double percent = 1.0 - (val.calculateQuantizedPeriod(period) / val.calculateQuantizedPeriod(topPeriod));
		int x = (int) ((double)(1.0-percent) * waveCycleWidth) - waveCycleWidth;
		int y = (int) ((double)percent * waveHeight);
		if (val.calculateQuantizedPeriod(period) > val.calculateQuantizedPeriod(val.getTopPeriod()))
			x = 1;
		return new Point (x, y);
	}
	
	private static int getYCoordinateFromPercentage(double percent) {
		// 0 percent is:
		// -> yOffset + waveHeight
		// 100 percent is:
		// -> yOffset

		int y = (int) (((double) (100.0 - percent)) * ((double)(waveHeight / 100.0)));
		y += yWaveOffset;
		return y;
	}
	
	private static int getYCoordinateForOutputPin(String register) {
		int y = yWaveOffset + waveHeight + yOutputPinPadding;
		if (register.endsWith("B"))
			y += 25;
		else if (register.endsWith("C"))
			y += 50;
		return y;
	}
	
	private static Color getColorForOutputPin(String register) {
		if (register.endsWith("B"))
			return COLOR_CHANNEL_B;
		if (register.endsWith("C"))
			return COLOR_CHANNEL_C;
		return COLOR_CHANNEL_A;
	}
}
