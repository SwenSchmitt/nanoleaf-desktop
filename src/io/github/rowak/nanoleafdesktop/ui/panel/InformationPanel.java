package io.github.rowak.nanoleafdesktop.ui.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import io.github.rowak.Aurora;
import io.github.rowak.StatusCodeException;
import io.github.rowak.nanoleafdesktop.Main;
import io.github.rowak.nanoleafdesktop.ui.button.ModernButton;
import io.github.rowak.nanoleafdesktop.ui.button.ModernToggleButton;
import io.github.rowak.nanoleafdesktop.ui.dialog.TextDialog;
import io.github.rowak.nanoleafdesktop.ui.dialog.colorpicker.BrightnessSlider;
import io.github.rowak.nanoleafdesktop.ui.dialog.colorpicker.ColorPicker;
import io.github.rowak.nanoleafdesktop.ui.dialog.colorpicker.ColorWheel;
import io.github.rowak.nanoleafdesktop.ui.listener.ComponentChangeListener;
import io.github.rowak.nanoleafdesktop.ui.panel.panelcanvas.PanelCanvas;
import io.github.rowak.nanoleafdesktop.ui.slider.ModernSliderUI;
import net.miginfocom.swing.MigLayout;

public class InformationPanel extends JPanel
{
	private boolean adjusting;
	private Main parent;
	private Aurora device;
	private PanelCanvas canvas;
	
	private JToggleButton btnOnOff;
	private JLabel lblActiveScene;
	private JSlider brightnessSlider;
	private JSlider ctSlider;
	
	public InformationPanel(Main parent, Aurora device, PanelCanvas canvas)
	{
		this.parent = parent;
		this.device = device;
		this.canvas = canvas;
		init();
	}
	
	public JToggleButton getBtnOnOff()
	{
		return btnOnOff;
	}
	
	public void setScene(String scene)
	{
		lblActiveScene.setText(scene);
	}
	
	public void setSliderBrightness(int brightness)
	{
		brightnessSlider.setValue(brightness);
	}
	
	public void setSliderColorTemp(int temp)
	{
		ctSlider.setValue(temp);
	}
	
	private void init()
	{
		setBorder(new LineBorder(Color.GRAY, 1, true));
		setBackground(Color.DARK_GRAY);
		setLayout(new MigLayout("", "[][428.00]", "[][][][][]"));
		
		JLabel lblOnOff = new JLabel("On/Off");
		lblOnOff.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblOnOff.setForeground(Color.WHITE);
		add(lblOnOff, "cell 0 0,aligny center");
		
		btnOnOff = new ModernToggleButton("Turn On");
		btnOnOff.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JToggleButton btn = (JToggleButton)e.getSource();
				if (btn.getText().equals("Turn On"))
					btn.setText("Turn Off");
				else
					btn.setText("Turn On");
				
				try
				{
					device.state().toggleOn();
					canvas.toggleOn();
				}
				catch (HttpRequestException hre)
				{
					new TextDialog(parent,
							"Lost connection to the device. " +
							"Please try again.").setVisible(true);
				}
				catch (StatusCodeException sce)
				{
					new TextDialog(parent,
							"The requested action could not be completed. " +
							"Please try again.").setVisible(true);
				}
			}
		});
		add(btnOnOff, "cell 1 0");
		
		JLabel lblCurrentScene = new JLabel("Active Scene:");
		lblCurrentScene.setForeground(Color.WHITE);
		lblCurrentScene.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblCurrentScene, "cell 0 1");
		
		lblActiveScene = new JLabel("*None*");
		lblActiveScene.setForeground(Color.WHITE);
		lblActiveScene.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblActiveScene, "cell 1 1");
		
		JLabel lblBrightness = new JLabel("Brightness");
		lblBrightness.setForeground(Color.WHITE);
		lblBrightness.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblBrightness, "cell 0 2");
		
		brightnessSlider = new JSlider();
		brightnessSlider.setBackground(Color.DARK_GRAY);
		brightnessSlider.setUI(new ModernSliderUI(brightnessSlider,
				Color.GRAY, Color.DARK_GRAY, Color.DARK_GRAY));
		brightnessSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!adjusting)
				{
					adjusting = true;
					new Thread(() ->
					{
						try
						{
							JSlider slider = (JSlider)e.getSource();
							if (slider.getValueIsAdjusting())
							{
								device.state().setBrightness(slider.getValue());
							}
							else
							{
								canvas.checkAuroraState();
								parent.loadActiveScene();
							}
							adjusting = false;
						}
						catch (HttpRequestException hre)
						{
							new TextDialog(parent,
									"Lost connection to the device. " +
									"Please try again.").setVisible(true);
						}
						catch (StatusCodeException sce)
						{
							new TextDialog(parent,
									"The requested action could not be completed. " +
									"Please try again.").setVisible(true);
						}
					}).start();
				}
			}
		});
		add(brightnessSlider, "cell 1 2,growx");
		
		JLabel lblColorTemperature = new JLabel("Color Temperature");
		lblColorTemperature.setForeground(Color.WHITE);
		lblColorTemperature.setFont(new Font("Tahoma", Font.PLAIN, 25));
		add(lblColorTemperature, "cell 0 3,gapx 0 15");
		
		ctSlider = new JSlider();
		ctSlider.setMaximum(6400);
		ctSlider.setMinimum(1200);
		ctSlider.setBackground(Color.DARK_GRAY);
		ctSlider.setUI(new ModernSliderUI(ctSlider,
				Color.GRAY, Color.DARK_GRAY, Color.DARK_GRAY));
		ctSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (!adjusting)
				{
					adjusting = true;
					new Thread(() ->
					{
						try
						{
							JSlider slider = (JSlider)e.getSource();
							if (slider.getValueIsAdjusting())
							{
								device.state().setColorTemperature(slider.getValue());
							}
							else
							{
								canvas.checkAuroraState();
								parent.loadActiveScene();
							}
						}
						catch (HttpRequestException hre)
						{
							new TextDialog(parent,
									"Lost connection to the device. " +
									"Please try again.").setVisible(true);
						}
						catch (StatusCodeException sce)
						{
							new TextDialog(parent,
									"The requested action could not be completed. " +
									"Please try again.").setVisible(true);
						}
						adjusting = false;
					}).start();
				}
			}
		});
		add(ctSlider, "cell 1 3,growx");
		
		JLabel lblSolidColor = new JLabel("Solid Color");
		lblSolidColor.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblSolidColor.setBackground(Color.DARK_GRAY);
		lblSolidColor.setForeground(Color.WHITE);
		add(lblSolidColor, "cell 0 4");
		
		JButton btnSetSolidColor = new ModernButton("Set Solid Color");
		btnSetSolidColor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JButton btn = (JButton)e.getSource();
				JFrame frame = (JFrame)btn.getFocusCycleRootAncestor();
				ColorPicker colorPicker = new ColorPicker(frame);
				colorPicker.setVisible(true);
				colorPicker.getColorWheel().addChangeListener(new ComponentChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						if (!adjusting)
						{
							adjusting = true;
							new Thread(() ->
							{
								ColorWheel wheel = (ColorWheel)e.getSource();
								Color color = wheel.getColor();
								float[] hsb = new float[3];
								hsb = Color.RGBtoHSB(color.getRed(),
										color.getGreen(), color.getBlue(), hsb);
								
								try
								{
									device.state().setHue((int)(hsb[0]*360));
									device.state().setSaturation((int)(hsb[1]*100));
									device.state().setBrightness((int)(hsb[2]*100));
									parent.loadStateComponents();
								}
								catch (HttpRequestException hre)
								{
									new TextDialog(parent,
											"Lost connection to the device. " +
											"Please try again.").setVisible(true);
								}
								catch (StatusCodeException sce)
								{
									new TextDialog(parent,
											"The requested action could not be completed. " +
											"Please try again.").setVisible(true);
								}
								
								canvas.setColor(color);
								adjusting = false;
							}).start();
						}
					}
				});
				colorPicker.getBrightnessSlider().addChangeListener(new ComponentChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						if (!adjusting)
						{
							adjusting = true;
							new Thread(() ->
							{
								BrightnessSlider slider = (BrightnessSlider)e.getSource();
								int brightness = slider.getValue();
								try
								{
									device.state().setBrightness(brightness);
									int hue = device.state().getHue();
									int sat = device.state().getSaturation();
									canvas.setColor(Color.getHSBColor(hue/360f, sat/100f, brightness/100f));
									parent.loadStateComponents();
								}
								catch (HttpRequestException hre)
								{
									new TextDialog(parent,
											"Lost connection to the device. " +
											"Please try again.").setVisible(true);
								}
								catch (StatusCodeException sce)
								{
									new TextDialog(parent,
											"The requested action could not be completed. " +
											"Please try again.").setVisible(true);
								}
								adjusting = false;
							}).start();
						}
					}
				});
			}
		});
		add(btnSetSolidColor, "cell 1 4");
	}
}