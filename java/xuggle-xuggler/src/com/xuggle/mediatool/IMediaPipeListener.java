/*
 * Copyright (c) 2008, 2009 by Xuggle Incorporated.  All rights reserved.
 * 
 * This file is part of Xuggler.
 * 
 * You can redistribute Xuggler and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Xuggler is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Xuggler.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.xuggle.mediatool;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/** 
 * IMediaPipeListener objects attach to {@link IMediaPipe} to receive
 * notification of significant events.
 * <p>
 * You can use objects that implement this interface to listen to events
 * generated by an {@link IMediaPipe} object.  To do that, create
 * your {@link IMediaPipeListener} object and attach it to an {@link IMediaPipe}
 * by calling {@link IMediaPipe#addListener(IMediaPipeListener)}.
 * </p>
 * <p>
 * 
 * These methods block the calling {@link IMediaPipe} while they process so
 * try to return quickly.  If you have long running actions to perform, use
 * a separate thread.
 * 
 * </p>
 */

public interface IMediaPipeListener
{
  /**
   * Called after a video frame has been decoded by a {@link
   * MediaReader} or encoded by a {@link IMediaWriter}.  Optionally a
   * BufferedImage version of the frame may be passed.
   *
   * @param pipe the pipe that generated this event
   * @param picture a raw video picture.  This picture will only be valid
   *   for the duration of this call.  If you need to remember the data, you
   *   must either copy it out of the video picture, or use {@link IVideoPicture#copyReference()}
   *   to create a new object that points to the same memory as picture.
   * @param image the buffered image, which may be null if conversion was
   *   not asked for.  If this value is not null, then it is safe for you
   *   to remember this object and use it after this call returns.
   *   
   * @param streamIndex the index of the stream this object was decoded from.
   */

  public void onVideoPicture(IMediaPipe pipe, IVideoPicture picture,
    BufferedImage image, int streamIndex);
  
  /**
   * Called after audio samples have been decoded or encoded by
   * an {@link IMediaPipe}
   *
   * @param pipe the pipe that generated this event
   * @param samples a set of audio samples.  The samples will only be valid
   *   for the duration of this call.  If you need to remember the data,
   *   you must either copy it out of the {@link IAudioSamples} object
   *   into your own object, or use {@link IAudioSamples#copyReference()}
   *   to tell Xuggler you want to maintain your own reference.
   * @param streamIndex the index of the stream
   */

  public void onAudioSamples(IMediaPipe pipe, IAudioSamples samples, 
    int streamIndex);

  /**
   * Called after an {@link IMediaPipe} is opened.
   *
   * @param pipe the pipe that generated this event
   */

  public void onOpen(IMediaPipe pipe);

  /**
   * Called after an {@link IMediaPipe} is closed.
   * 
   * @param pipe the pipe that generated this event
   */

  public void onClose(IMediaPipe pipe);

  /**
   * Called after an stream is added to an
   * {@link IMediaPipe}.  This occurs when
   * a new stream is added (if writing) or encountered by the pipe (if reading).
   * If the stream is not already been opened, then {@link
   * #onOpenCoder(IMediaPipe, IStreamCoder)} will be called at some later point.
   *
   * @param pipe the pipe that generated this event
   * @param streamIndex the stream opened
   */

  public void onAddStream(IMediaPipe pipe, int streamIndex);

  /**
   * Called after an {@link IStreamCoder } is opened by
   * a {@link IMediaPipe}
   *
   * @param pipe the pipe that generated this event
   * @param coder the stream opened
   */

  public void onOpenCoder(IMediaPipe pipe, IStreamCoder coder);

  /**
   * Called after an {@link IStreamCoder} is closed by the {@link IMediaPipe}.
   *
   * @param pipe the pipe that generated this event
   * @param coder the stream just closed
   */

  public void onCloseCoder(IMediaPipe pipe, IStreamCoder coder);

  /**
   * Called after a {@link com.xuggle.xuggler.IPacket} has been read by
   * a {@link IMediaReader}.
   *
   * @param pipe the pipe that generated this event
   * @param packet the packet just read.  This {@link IPacket} is only
   *   valid for the duration of this call.  If you need to use
   *   the data after this call has returned, you must either copy
   *   the data in this call, or use {@link IPacket#copyReference()} to
   *   tell Xuggler you want your own reference to the data.
   */

  public void onReadPacket(IMediaPipe pipe, IPacket packet);

  /**
   * Called after a {@link com.xuggle.xuggler.IPacket} has been written
   * by a {@link IMediaWriter}.
   *
   * @param pipe the pipe that generated this event
   * @param packet the packet just written.    This {@link IPacket} is only
   *   valid for the duration of this call.  If you need to use
   *   the data after this call has returned, you must either copy
   *   the data in this call, or use {@link IPacket#copyReference()} to
   *   tell Xuggler you want your own reference to the data.
   */

  public void onWritePacket(IMediaPipe pipe, IPacket packet);

  /**
   * Called after a {@link IMediaWriter} writes the header.
   *
   * @param pipe the pipe that generated this event
   */

  public void onWriteHeader(IMediaPipe pipe);

  /**
   * Called after a {@link IMediaWriter} has flushed its buffers.
   *
   * @param pipe the pipe that generated this event
   */

  public void onFlush(IMediaPipe pipe);

  /**
   * Called after a {@link IMediaWriter} writes the trailer.
   *
   * @param pipe the pipe that generated this event
   */

  public void onWriteTrailer(IMediaPipe pipe);
}