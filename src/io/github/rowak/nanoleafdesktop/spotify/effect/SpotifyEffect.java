package io.github.rowak.nanoleafdesktop.spotify.effect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.rowak.Aurora;
import io.github.rowak.Color;
import io.github.rowak.Panel;
import io.github.rowak.StatusCodeException;
import io.github.rowak.nanoleafdesktop.spotify.SpecificAudioAnalysis;
import io.github.rowak.nanoleafdesktop.spotify.SpotifyEffectType;
import io.github.rowak.nanoleafdesktop.spotify.UserOption;

public abstract class SpotifyEffect
{
	protected int paletteIndex;
	protected SpotifyEffectType type;
	protected Aurora aurora;
	protected Panel[] panels;
	protected Color[] palette;
	protected List<UserOption> userOptions;
	
	public SpotifyEffect(SpotifyEffectType type,
			Color[] palette, Aurora aurora)
	{
		this.type = type;
		this.palette = palette;
		this.aurora = aurora;
		try
		{
			panels = aurora.panelLayout().getPanels();
		}
		catch (StatusCodeException sce)
		{
			panels = new Panel[0];
		}
		userOptions = new ArrayList<UserOption>();
	}
	
	public abstract void init()
			throws StatusCodeException, IOException;
	
	public abstract void run(SpecificAudioAnalysis analysis)
			throws StatusCodeException, IOException;
	
	public SpotifyEffectType getType()
	{
		return type;
	}
	
	public List<UserOption> getUserOptions()
	{
		return userOptions;
	}
	
	public void setPalette(Color[] palette)
			throws IOException, StatusCodeException
	{
		this.palette = palette;
		paletteIndex = palette.length > 1 ? 1 : 0;
		init();
	}
	
	protected void setNextPaletteColor()
	{
		if (paletteIndex == palette.length-1)
		{
			paletteIndex = 0;
		}
		else
		{
			paletteIndex++;
		}
	}
}
