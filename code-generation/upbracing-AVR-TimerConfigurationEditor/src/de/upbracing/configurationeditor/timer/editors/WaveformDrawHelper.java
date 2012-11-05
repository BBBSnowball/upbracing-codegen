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
import de.upbracing.shared.timer.model.enums.CTCTopValues;
import de.upbracing.shared.timer.model.enums.PWMDualSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMSingleSlopeOutputPinMode;
import de.upbracing.shared.timer.model.enums.PWMTopValues;
import de.upbracing.shared.timer.model.enums.PhaseAndFrequencyCorrectPWMTopValues;
import de.upbracing.shared.timer.model.enums.TimerEnum;
import de.upbracing.shared.timer.model.enums.TimerOperationModes;

/**
 * @author Peer Adelt (adelt@mail.uni-paderborn.de)
 *
 */
public class WaveformDrawHelper {
	
	// Width of one slope
	private static int cycleWidth = 60;
	// Horizontal and vertical offset of waveform
	private static int yOffset = 35;
	private static int xOffset = 115;
	
	// Used colors
	private static Color COLOR_DARK_GRAY 
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	private static Color COLOR_BLACK
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK);
	private static Color COLOR_CYAN
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_CYAN);
	private static Color COLOR_DEFAULT_BG
		= Activator.getDefault().getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	
	public static void drawWaveform(GC gc, boolean dualSlope) {
		gc.setLineWidth(2);
		if (!dualSlope) {
		    gc.drawPolyline(new int[] { 
		    		xOffset,                  90 + yOffset, 
		    		xOffset + cycleWidth,     10 + yOffset, 
		    		xOffset + cycleWidth,     90 + yOffset, 
		    		xOffset + cycleWidth * 2, 10 + yOffset, 
		    		xOffset + cycleWidth * 2, 90 + yOffset, 
		    		xOffset + cycleWidth * 3, 10 + yOffset, 
		    		xOffset + cycleWidth * 3, 90 + yOffset, 
		    		xOffset + cycleWidth * 4, 10 + yOffset, 
		    		xOffset + cycleWidth * 4, 90 + yOffset });
		} else {
			gc.drawPolyline(new int[] { 
					xOffset,                  90 + yOffset, 
		    		xOffset + cycleWidth,     10 + yOffset, 
		    		xOffset + cycleWidth * 2, 90 + yOffset, 
		    		xOffset + cycleWidth * 3, 10 + yOffset, 
		    		xOffset + cycleWidth * 4, 90 + yOffset });
		}
	}
	
	public static void drawHorizontalLine(GC gc, int y, String txt) {
		drawHorizontalLine(gc, y, txt, 1, false);
	}
	
	public static void drawHorizontalLine(GC gc, int y, String txt, int linecount, boolean alignBottom) {
	    gc.setLineWidth(1);
	    gc.setAntialias(SWT.OFF);
	    gc.setLineStyle(SWT.LINE_DASH);
	    gc.setForeground(COLOR_DARK_GRAY);
	    gc.drawLine(xOffset - 5, 10 + yOffset + y, xOffset + 5 + cycleWidth * 4, 10 + yOffset + y);
	    int lineheight = gc.getFontMetrics().getHeight();
	    if (alignBottom) {
	    	gc.drawText(txt, 5, 3 + yOffset + y - ((linecount - 1) * lineheight), true);
	    } else {
	    	gc.drawText(txt, 5, 3 + yOffset + y, true);
	    }
	    gc.setAntialias(SWT.ON);
	}
	
	public static void drawPeriodText(GC gc, String txt, boolean dualSlope) {
		gc.setForeground(COLOR_BLACK);
		int stretchfactor = 2;
		if (dualSlope) {
			stretchfactor = 3;
		}
		gc.drawLine(xOffset + cycleWidth, yOffset - 15, xOffset + cycleWidth, 10 + yOffset - 10);
		gc.drawLine(xOffset + cycleWidth * stretchfactor, yOffset - 15, xOffset + cycleWidth * stretchfactor, 10 + yOffset - 10);
		gc.drawPolyline(new int[] {xOffset + cycleWidth + 2, 10 + yOffset - 17, xOffset + cycleWidth + 7, yOffset - 12});
		gc.drawPolyline(new int[] {xOffset + cycleWidth + 2, 10 + yOffset - 17, xOffset + cycleWidth + 7, yOffset - 3});
		gc.drawPolyline(new int[] {xOffset + cycleWidth * stretchfactor - 2, 10 + yOffset - 17, xOffset + cycleWidth * stretchfactor - 7, yOffset - 12});
		gc.drawPolyline(new int[] {xOffset + cycleWidth * stretchfactor - 2, 10 + yOffset - 17, xOffset + cycleWidth * stretchfactor - 7, yOffset - 3});
		gc.drawLine(xOffset + cycleWidth + 3, 10 + yOffset - 17, xOffset + cycleWidth * stretchfactor - 3, 10 + yOffset - 17);
		Point overflowPeriodStringWidth = gc.stringExtent(txt);
		int xDiff = (cycleWidth - overflowPeriodStringWidth.x) / 2;
		gc.drawText(txt, xOffset + ((int)(cycleWidth * (stretchfactor / 2.0))) + xDiff, 5, true);
	}
	
	public static void drawResetInterrupts(GC gc, boolean enabled) {
		gc.setLineStyle(SWT.LINE_SOLID);
	    gc.setLineWidth(3);
	    if (enabled) {
	    	gc.setBackground(COLOR_CYAN);
	    }
	    gc.fillOval(xOffset + cycleWidth - 3,     87 + yOffset, 6, 6);
    	gc.fillOval(xOffset + cycleWidth * 2 - 3, 87 + yOffset, 6, 6);
    	gc.fillOval(xOffset + cycleWidth * 3 - 3, 87 + yOffset, 6, 6);
    	gc.fillOval(xOffset + cycleWidth * 4 - 3, 87 + yOffset, 6, 6);
	    gc.setForeground(COLOR_BLACK);
	    gc.drawOval(xOffset + cycleWidth - 4,     86 + yOffset, 8, 8);
    	gc.drawOval(xOffset + cycleWidth * 2 - 4, 86 + yOffset, 8, 8);
    	gc.drawOval(xOffset + cycleWidth * 3 - 4, 86 + yOffset, 8, 8);
    	gc.drawOval(xOffset + cycleWidth * 4 - 4, 86 + yOffset, 8, 8);
	}
	
	public static void drawOverflowInterruptText(GC gc, boolean enabled) {
		String overflowInterruptText = "Overflow interrupt ";
    	if (enabled) {
    		overflowInterruptText += "enabled";
    	} else {
    		overflowInterruptText += "disabled";
    	}
    	int xDiff = (3 * cycleWidth - gc.stringExtent(overflowInterruptText).x) / 2;
    	gc.drawText(overflowInterruptText, xOffset + cycleWidth + xDiff, 100 + yOffset, true);
	}
	
	public static void drawTopLine(GC gc, UseCaseViewModel model) {
		String ls = System.getProperty("line.separator");
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
		
		boolean startedWithChannels = false;
		String lineText = "TOP (" + model.getValidator().calculateRegisterValue(model.getValidator().getTopPeriod()) + ")";
		if (lst.size() > 0)
			lineText += ls;
		for (String entry : lst) {
			if (!entry.equals("ICR")) {
				if (!startedWithChannels) {
					lineText += "Channel ";
					startedWithChannels = true; 
				}
			}
			lineText += entry;
			if (!lst.get(lst.size()-1).equals(entry))
				lineText += ", ";
		}
		if (lst.size() > 0)
			WaveformDrawHelper.drawHorizontalLine(gc, 0, lineText, 2, true);
		else 
			WaveformDrawHelper.drawHorizontalLine(gc, 0, lineText);
	}
	
	public static void drawChannels(GC gc, UseCaseViewModel model) {
		
		drawTopLine(gc, model);
		
		// 1) Get Top value:
		double topPeriod = model.getValidator().getTopPeriod();
		// 2) Put all periods in hashmap
		HashMap<Double, String> hm = getRelevantChannels(model);
		// Draw the sorted list
		TreeSet<Double> periods = new TreeSet<Double>(hm.keySet());
		int count = periods.size();
		int span = 80 / (count + 1);
		int counter = 1;
		for (Double period : periods.descendingSet()) {
			String value = hm.get(period);
			Point p;
			boolean compareInterrupt = false;
			if (value.substring(7).contains("A"))
				compareInterrupt = model.getCompareInterruptA();
			if (value.substring(7).contains("B"))
				compareInterrupt |= model.getCompareInterruptB();
			if (value.substring(7).contains("C"))
				compareInterrupt |= model.getCompareInterruptC();
			if (count > 1) {
				// Line:
				WaveformDrawHelper.drawHorizontalLine(gc, counter * span, value + " (" + model.getValidator().calculateRegisterValue(period) +")");
				if (model.getMode().equals(TimerOperationModes.CTC)) {
					// Interrupt bullets:
//					Point p = new Point(0, counter * span);
					p = getSectionPoint(model, value);
					drawChannelInterrupts(gc, p, compareInterrupt);
				}
				counter++;
			} else {
				if (period < (topPeriod / 2)) {
					WaveformDrawHelper.drawHorizontalLine(gc, 50, value + " (" + model.getValidator().calculateRegisterValue(period) +")");
					if (model.getMode().equals(TimerOperationModes.CTC)) {
//						// Interrupt bullets:
//						Point p = new Point(0, 50);
						p = getSectionPoint(model, value);
						drawChannelInterrupts(gc, p, compareInterrupt);
					}
				} else {
					WaveformDrawHelper.drawHorizontalLine(gc, 30, value + " (" + model.getValidator().calculateRegisterValue(period) +")");
					if (model.getMode().equals(TimerOperationModes.CTC)) {
//						// Interrupt bullets:
//						Point p = new Point(0, 30);
						p = getSectionPoint(model, value);
						drawChannelInterrupts(gc, p, compareInterrupt);
					}
				}
			}
		}
	}
	
	public static void drawCTCOutputPin(GC gc, UseCaseViewModel model, String register) {
		int y = yOffset + 110;
		if (register.endsWith("B"))
			y += 25;
		else if (register.endsWith("C"))
			y += 50;
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(model, register);
		if (p != null) {
			if (register.equals("Channel A")) {
				if (model.getComparePinModeA().equals(CTCOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, p.x, y, false);
				} else {
					gc.drawString(model.getComparePinModeA().toString(), xOffset, y, true);
				}
			}
			else if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (register.equals("Channel B")) {
					if (model.getComparePinModeB().equals(CTCOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, false);
					} else {
						gc.drawString(model.getComparePinModeB().toString(), xOffset, y, true);
					}
				} else if (register.equals("Channel C")) {
					if (model.getComparePinModeC().equals(CTCOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, false);
					} else {
						gc.drawString(model.getComparePinModeC().toString(), xOffset, y, true);
					}
				}
			} else {
				gc.drawString("N/A", xOffset, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xOffset, y, true);
		}
	}
	
	public static void drawSingleSlopePWMOutputPin(GC gc, UseCaseViewModel model, String register) {
		int y = yOffset + 110;
		if (register.endsWith("B"))
			y += 25;
		else if (register.endsWith("C"))
			y += 50;
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(model, register);
		if (p != null) {
			if (register.equals("Channel A")) {
				if (model.getSingleSlopePWMPinModeA().equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, p.x, y, false);
				} else if (model.getSingleSlopePWMPinModeA().equals(PWMSingleSlopeOutputPinMode.CLEAR)) {
					drawOutputPinPWMLine(gc, p.x, y, false, false);
				} else if (model.getSingleSlopePWMPinModeA().equals(PWMSingleSlopeOutputPinMode.SET)) {
					drawOutputPinPWMLine(gc, p.x, y, false, true);
				} else {
					gc.drawString(model.getComparePinModeA().toString(), xOffset, y, true);
				}
			}
			else if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (register.equals("Channel B")) {
					if (model.getSingleSlopePWMPinModeB().equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, false);
					} else if (model.getSingleSlopePWMPinModeB().equals(PWMSingleSlopeOutputPinMode.CLEAR)) {
						drawOutputPinPWMLine(gc, p.x, y, false, false);
					} else if (model.getSingleSlopePWMPinModeB().equals(PWMSingleSlopeOutputPinMode.SET)) {
						drawOutputPinPWMLine(gc, p.x, y, false, true);
					} else {
						gc.drawString(model.getComparePinModeB().toString(), xOffset, y, true);
					}
				} else if (register.equals("Channel C")) {
					if (model.getSingleSlopePWMPinModeC().equals(PWMSingleSlopeOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, false);
					} else if (model.getSingleSlopePWMPinModeC().equals(PWMSingleSlopeOutputPinMode.CLEAR)) {
						drawOutputPinPWMLine(gc, p.x, y, false, false);
					} else if (model.getSingleSlopePWMPinModeC().equals(PWMSingleSlopeOutputPinMode.SET)) {
						drawOutputPinPWMLine(gc, p.x, y, false, true);
					} else {
						gc.drawString(model.getComparePinModeC().toString(), xOffset, y, true);
					}
				}
			} else {
				gc.drawString("N/A", xOffset, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xOffset, y, true);
		}
	}
	
	public static void drawDualSlopePWMOutputPin(GC gc, UseCaseViewModel model, String register) {
		int y = yOffset + 110;
		if (register.endsWith("B"))
			y += 25;
		else if (register.endsWith("C"))
			y += 50;
		gc.setForeground(COLOR_BLACK);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.drawText(register.replace("Channel", "Output pin"), 5, y, true);
		Point p = getSectionPoint(model, register);
		if (p != null) {
			if (register.equals("Channel A")) {
				if (model.getDualSlopePWMPinModeA().equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
					drawOutputPinToggleLine(gc, p.x, y, true);
				} else if (model.getDualSlopePWMPinModeA().equals(PWMDualSlopeOutputPinMode.CLEAR_SET)) {
					drawOutputPinPWMLine(gc, p.x, y, true, false);
				} else if (model.getDualSlopePWMPinModeA().equals(PWMDualSlopeOutputPinMode.SET_CLEAR)) {
					drawOutputPinPWMLine(gc, p.x, y, true, true);
				} else {
					gc.drawString(model.getComparePinModeA().toString(), xOffset, y, true);
				}
			}
			else if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
				if (register.equals("Channel B")) {
					if (model.getDualSlopePWMPinModeB().equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, true);
					} else if (model.getDualSlopePWMPinModeB().equals(PWMDualSlopeOutputPinMode.CLEAR_SET)) {
						drawOutputPinPWMLine(gc, p.x, y, true, false);
					} else if (model.getDualSlopePWMPinModeB().equals(PWMDualSlopeOutputPinMode.SET_CLEAR)) {
						drawOutputPinPWMLine(gc, p.x, y, true, true);
					} else {
						gc.drawString(model.getComparePinModeB().toString(), xOffset, y, true);
					}
				} else if (register.equals("Channel C")) {
					if (model.getDualSlopePWMPinModeC().equals(PWMDualSlopeOutputPinMode.TOGGLE)) {
						drawOutputPinToggleLine(gc, p.x, y, true);
					} else if (model.getDualSlopePWMPinModeC().equals(PWMDualSlopeOutputPinMode.CLEAR_SET)) {
						drawOutputPinPWMLine(gc, p.x, y, true, false);
					} else if (model.getDualSlopePWMPinModeC().equals(PWMDualSlopeOutputPinMode.SET_CLEAR)) {
						drawOutputPinPWMLine(gc, p.x, y, true, true);
					} else {
						gc.drawString(model.getComparePinModeC().toString(), xOffset, y, true);
					}
				}
			} else {
				gc.drawString("N/A", xOffset, y, true);
			}
		} else {
			gc.drawString("See error or warning message for details!", xOffset, y, true);
		}
	}
	
	private static void drawOutputPinToggleLine(GC gc, int x, int y, boolean dualSlope) {
		gc.setLineWidth(2);
		int xStart = xOffset;
		int xOff = xOffset + x + cycleWidth;
		int yLow = y + 14;
		int yHigh = y;
		if (!dualSlope) {
			gc.drawPolyline(new int[] {
					xStart, yLow, 
					xOff, yLow, 
					xOff, yHigh, 
					xOff + cycleWidth, yHigh,
					xOff + cycleWidth, yLow, 
					xOff + cycleWidth * 2, yLow, 
					xOff + cycleWidth * 2, yHigh, 
					xOff + cycleWidth * 3, yHigh,
					xOff + cycleWidth * 3, yLow,
					xStart + cycleWidth * 4, yLow});
		} else {
			gc.drawPolyline(new int[] {
					xStart, yLow, 
					xOff, yLow,
					xOff, yHigh,
					xOff + cycleWidth * 2, yHigh, 
					xOff + cycleWidth * 2, yLow, 
					xStart + cycleWidth * 4, yLow});
		}
	}
	
	private static void drawOutputPinPWMLine(GC gc, int x, int y, boolean dualSlope, boolean inverted) {
		gc.setLineWidth(2);
		int xStart = xOffset;
		int xOff = xOffset + x + cycleWidth;
		int yLow = y;
		int yHigh = y + 14;
		if (inverted) {
			yLow = y + 14;
			yHigh = y;
		}
		if (!dualSlope) {
			gc.drawPolyline(new int[] {
					xStart, yLow, 
					xOff, yLow, 
					xOff, yHigh, 
					xStart + cycleWidth, yHigh,
					xStart + cycleWidth, yLow, 
					xOff + cycleWidth, yLow, 
					xOff + cycleWidth, yHigh, 
					xStart + cycleWidth * 2, yHigh,
					xStart + cycleWidth * 2, yLow,
					xOff + cycleWidth * 2, yLow,
					xOff + cycleWidth * 2, yHigh,
					xStart + cycleWidth * 3, yHigh,
					xStart + cycleWidth * 3, yLow,
					xOff + cycleWidth * 3, yLow,
					xOff + cycleWidth * 3, yHigh,
					xStart + cycleWidth * 4, yHigh,
					xStart + cycleWidth * 4, yLow});
		} else {
			gc.drawPolyline(new int[] {
					xStart, yLow, 
					xOff, yLow,
					xOff, yHigh,
					(xStart + cycleWidth) * 2 - xOff, yHigh,
					(xStart + cycleWidth) * 2 - xOff, yLow,
					xOff + cycleWidth * 2, yLow,
					xOff + cycleWidth * 2, yHigh,
					2 * xStart + cycleWidth * 4 - xOff, yHigh,
					2 * xStart + cycleWidth * 4 - xOff, yLow,
					xStart + cycleWidth * 4, yLow});
		}
	}
	
	private static void drawChannelInterrupts(GC gc, Point p, boolean enabled) {
		gc.setLineStyle(SWT.LINE_SOLID);
	    gc.setLineWidth(3);
	    if (enabled) {
	    	gc.setBackground(COLOR_CYAN);
	    } else {
	    	gc.setBackground(COLOR_DEFAULT_BG);
	    }
	    gc.fillOval(p.x + xOffset + cycleWidth - 3,     p.y + yOffset + 6, 6, 6);
		gc.fillOval(p.x + xOffset + cycleWidth * 2 - 3, p.y + yOffset + 6, 6, 6);
		gc.fillOval(p.x + xOffset + cycleWidth * 3 - 3, p.y + yOffset + 6, 6, 6);
		gc.fillOval(p.x + xOffset + cycleWidth * 4 - 3, p.y + yOffset + 6, 6, 6);
	    gc.setForeground(COLOR_BLACK);
	    gc.drawOval(p.x + xOffset + cycleWidth - 4,     p.y + yOffset + 6, 8, 8);
		gc.drawOval(p.x + xOffset + cycleWidth * 2 - 4, p.y + yOffset + 6, 8, 8);
		gc.drawOval(p.x + xOffset + cycleWidth * 3 - 4, p.y + yOffset + 6, 8, 8);
		gc.drawOval(p.x + xOffset + cycleWidth * 4 - 4, p.y + yOffset + 6, 8, 8);
	}

	private static boolean isChannelARelevant(UseCaseViewModel model) {
		if ((model.getMode().equals(TimerOperationModes.CTC) && !model.getCtcTop().equals(CTCTopValues.OCRnA)) ||
			(model.getMode().equals(TimerOperationModes.PWM_FAST) && !model.getFastPWMTop().equals(PWMTopValues.OCRnA)) ||
			(model.getMode().equals(TimerOperationModes.PWM_PHASE_CORRECT) && !model.getPhaseCorrectPWMTop().equals(PWMTopValues.OCRnA)) ||
			(model.getMode().equals(TimerOperationModes.PWM_PHASE_FREQUENCY_CORRECT) && !model.getPhaseAndFrequencyCorrectPWMTop().equals(PhaseAndFrequencyCorrectPWMTopValues.OCRnA))) {
			return true;
		}
		return false;
	}
	
	private static HashMap<Double, String> getRelevantChannels(UseCaseViewModel model) {
		// 1) Get top period
		double topPeriod = model.getValidator().getTopPeriod();
		// 2) Put all periods in hashmap
		HashMap<Double, String> hm = new HashMap<Double, String>();
		if (isChannelARelevant(model))
			hm.put(model.getOcrAPeriod(), "Channel A");
		if (model.getTimer().equals(TimerEnum.TIMER1) || model.getTimer().equals(TimerEnum.TIMER3)) {
			if (hm.containsKey(model.getOcrBPeriod()))
				hm.put(model.getOcrBPeriod(), hm.get(model.getOcrBPeriod()) + ", B");
			else
				hm.put(model.getOcrBPeriod(), "Channel B");
			if (hm.containsKey(model.getOcrCPeriod()))
				hm.put(model.getOcrCPeriod(), hm.get(model.getOcrCPeriod()) + ", C");
			else
				hm.put(model.getOcrCPeriod(), "Channel C");
		}
		// 3) Cleanup sorted list
		TreeSet<Double> periods = new TreeSet<Double>(hm.keySet());
		for (Double period : periods) {
			if (model.getValidator().calculateQuantizedPeriod(period) >= model.getValidator().calculateQuantizedPeriod(topPeriod)) {
				hm.remove(period);
			}
		}
		return hm;
	}
	
	private static Point getSectionPoint(UseCaseViewModel model, String register) {
		
		double topPeriodCompare = -1.0;
		if (register.equals("Channel A")) 
			topPeriodCompare = model.getOcrAPeriod();
		if (register.equals("Channel B")) 
			topPeriodCompare = model.getOcrBPeriod();
		if (register.equals("Channel C")) 
			topPeriodCompare = model.getOcrCPeriod();
		if (topPeriodCompare == model.getValidator().getTopPeriod())
			return new Point(0, 0);
		
		HashMap<Double, String> hm = getRelevantChannels(model);
		int count = hm.size();
		TreeSet<Double> periods = new TreeSet<Double>(hm.keySet());
		int i = 0;
		double p = -1;
		for (Double period : periods.descendingSet()) {
			String value = hm.get(period);
			String channelChar = register.subSequence(register.length() - 1, register.length()).toString();
			if (value.startsWith("Channel ") &&
					value.substring(1).contains(channelChar)) {
				p = period;
				break;
			}
			i++;
		}
		
		// Get y coordinate:
		int y = -1;
		if (count == 3) {
			if (i == 2)
				y = 60;
			if (i == 1)
				y = 40;
			if (i == 0)
				y = 20;
		} else if (count == 2) {
			if (i == 1)
				y = 53;
			if (i == 0)
				y = 27;
		} else if (count == 1) {
			if (p < (model.getValidator().getTopPeriod() / 2))
				y = 50;
			else
				y = 30;
		}
		
		// Get x coordinate:
		y--;
		double m = 75.0/cycleWidth;
		double a = (80 - y);
		double c = a * m;
		int x = (int) Math.sqrt(Math.pow(c, 2) - Math.pow(a, 2)) - cycleWidth;
		
		if (p == -1) {
			return null;
		}
		return new Point(x, y);
	}
}
