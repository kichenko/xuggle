/*
 * Copyright (c) 2008-2009 by Xuggle Inc. All rights reserved.
 *
 * It is REQUESTED BUT NOT REQUIRED if you use this library, that you let 
 * us know by sending e-mail to info@xuggle.com telling us briefly how you're
 * using the library and what you like or don't like about it.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.xuggle.xuggler.demos;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IConfigurable;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IProperty;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * Opens up a media container, and prints out a summary of the contents.
 * 
 * If you pass -Dxuggle.options we'll also tell you what every
 * configurable option on the container and streams is set to.
 * 
 * @author aclarke
 *
 */
public class GetContainerInfo
{
  /**
   * Takes a media container (file) as the first argument, opens it, and tells you what's inside the container.
   * @param args Must contain one string which represents a filename
   */
  public static void main(String[] args)
  {
    if (args.length <= 0)
      throw new IllegalArgumentException("must pass in a filename as the first argument");
    
    String filename = args[0];
    
    // Create a Xuggler container object
    IContainer container = IContainer.make();
    
    // Open up the container
    if (container.open(filename, IContainer.Type.READ, null) < 0)
      throw new IllegalArgumentException("could not open file: " + filename);
    
    // query how many streams the call to open found
    int numStreams = container.getNumStreams();
    System.out.printf("file \"%s\": %d stream%s; ",
        filename,
        numStreams,
        numStreams == 1 ? "" : "s");
    System.out.printf("duration (ms): %d; ", container.getDuration()/1000);
    System.out.printf("start time: %d; ", container.getStartTime());
    System.out.printf("file size (bytes): %d; ", container.getFileSize());
    System.out.printf("bit rate: %d; ", container.getBitRate());
    System.out.printf("\n");

    // and iterate through the streams to print their meta data
    for(int i = 0; i < numStreams; i++)
    {
      // Find the stream object
      IStream stream = container.getStream(i);
      // Get the pre-configured decoder that can decode this stream;
      IStreamCoder coder = stream.getStreamCoder();
      
      // and now print out the meta data.
      System.out.printf("stream %d: ",    i);
      System.out.printf("type: %s; ",     coder.getCodecType());
      System.out.printf("codec: %s; ",    coder.getCodecID());
      System.out.printf("duration: %s; ", stream.getDuration() == Global.NO_PTS ? "unknown" : "" + stream.getDuration());
      System.out.printf("language: %s; ", stream.getLanguage() == null ? "unknown" : stream.getLanguage());
      System.out.printf("timebase: %d/%d; ", stream.getTimeBase().getNumerator(), stream.getTimeBase().getDenominator());
      System.out.printf("coder tb: %d/%d; ", coder.getTimeBase().getNumerator(), coder.getTimeBase().getDenominator());
      
      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
      {
        System.out.printf("sample rate: %d; ", coder.getSampleRate());
        System.out.printf("channels: %d; ",    coder.getChannels());
        System.out.printf("format: %s",        coder.getSampleFormat());
      } else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
      {
        System.out.printf("width: %d; ",  coder.getWidth());
        System.out.printf("height: %d; ", coder.getHeight());
        System.out.printf("format: %s; ", coder.getPixelType());
        System.out.printf("frame-rate: %5.2f; ", coder.getFrameRate().getDouble());
      }
      System.out.printf("\n");
    }
    
    // If the user passes -Dxuggle.options, then we print
    // out all possible options as well.
    String optionString = System.getProperty("xuggle.options");
    if (optionString != null)
      printOptions(container);
  }

  /**
   * This method iterates through the container, and prints out
   * available options for the container, and each stream.
   * 
   * @param aContainer Container to print options for.
   */
  private static void printOptions(IContainer aContainer)
  {
    
    System.out.printf("\n");
    System.out.printf("IContainer Options:\n");
    int numOptions = aContainer.getNumProperties();
    
    for(int i = 0; i < numOptions; i++)
    {
      IProperty prop = aContainer.getPropertyMetaData(i);
      printOption(aContainer, prop);
    }
    System.out.printf("\n");
    
    int numStreams = aContainer.getNumStreams();
    for(int i = 0; i < numStreams; i++)
    {
      IStreamCoder coder = aContainer.getStream(i).getStreamCoder();
      System.out.printf("IStreamCoder options for Stream %d of type %s:\n",
          i,
          coder.getCodecType());
      numOptions = coder.getNumProperties();
      for(int j = 0; j < numOptions; j++)
      {
        IProperty prop = coder.getPropertyMetaData(j);
        printOption(coder, prop);
      }
    }
  }

  private static void printOption(IConfigurable configObj, IProperty aProp)
  {
    if (aProp.getType() != IProperty.Type.PROPERTY_FLAGS)
    {
      System.out.printf("  %s: %s\n",
          aProp.getName(),
          configObj.getPropertyAsString(aProp.getName()));
    } else {
      // it's a flag
      System.out.printf("  %s: %d (", aProp.getName(),
          configObj.getPropertyAsLong(aProp.getName()));
      int numSettings = aProp.getNumFlagSettings();
      long value = configObj.getPropertyAsLong(aProp.getName());
      for(int i = 0; i < numSettings; i++)
      {
        IProperty prop = aProp.getFlagConstant(i);
        long flagMask = prop.getDefault();
        boolean isSet = (value & flagMask)>0;
        System.out.printf("%s%s; ",
            isSet ? "+" : "-",
                prop.getName());
      }
      System.out.printf(")\n");
    }
  }
}