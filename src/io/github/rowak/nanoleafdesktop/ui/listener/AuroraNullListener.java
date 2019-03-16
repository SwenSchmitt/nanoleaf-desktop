package io.github.rowak.nanoleafdesktop.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import io.github.rowak.nanoleafdesktop.ui.panel.DiscoveryPanel;
import io.github.rowak.nanoleafdesktop.ui.panel.KeyShortcutsPanel;
import io.github.rowak.nanoleafdesktop.ui.panel.SpotifyPanel;
import io.github.rowak.nanoleafdesktop.ui.panel.ambilight.AmbilightPanel;
import io.github.rowak.nanoleafdesktop.ui.panel.panelcanvas.PanelCanvas;

public class AuroraNullListener extends Timer
{
	public AuroraNullListener(int delay, ActionListener listener,
			PanelCanvas canvas, DiscoveryPanel discoveryPanel,
			AmbilightPanel ambilightPanel, SpotifyPanel spotifyPanel,
			KeyShortcutsPanel shortcutsPanel)
	{
		super(delay, listener);
		
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (canvas.getAurora() != null)
				{
					discoveryPanel.setAurora(canvas.getAurora());
					ambilightPanel.setAurora(canvas.getAurora());
					spotifyPanel.setAurora(canvas.getAurora());
					shortcutsPanel.setAurora(canvas.getAurora());
					canvas.initCanvas();
					canvas.repaint();
					
					AuroraNullListener.this.stop();
				}
			}
		});
	}
}
