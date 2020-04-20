package com.scoreunit.rfb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scoreunit.rfb.encoding.EncodingInterface;
import com.scoreunit.rfb.encoding.Encodings;
import com.scoreunit.rfb.encoding.HextileEncoder;
import com.scoreunit.rfb.encoding.RawEncoder;

/**
 * Here is algorithm how to select encoder for frame buffer.
 * <p>
 * It is based of last used encoder, if set and client supports last used encoder,
 * then it is selected.
 * <p>
 * If last used encoder is not (yet) set, then preferred list of encodings (server-side) 
 * is examined, and if found encoder in preferred list that is supported by client,
 * encoder is selected.
 * <p>
 * If last used encoder is not (yet) set, and preferred encoding list is also not set,
 * then client list of supported encodings is examined in order to find suitable encoder.
 * 
 * @author igor.delac@gmail.com
 *
 */
class SelectEncoder {
	
	public final static Logger log = LoggerFactory.getLogger(SelectEncoder.class);
	
	/**
	 * Method will select appropriate {@link EncodingInterface} instance,
	 * based on {@link FramebufferUpdater#clientEncodings} and {@link FramebufferUpdater#preferredEncodings}.
	 * <p>
	 * It will also consider {@link FramebufferUpdater#lastEncoder} reference.
	 *
	 * @param	lastEncoder		- 	instance of {@link EncodingInterface}, eg. {@link RawEncoder}, or null value
	 * @param	clientEncodings	-	list of VNC client supported encodings, mandatory
	 * @param	preferredEncodings	-	list of preferred encodings, might be null if not set
	 * 
	 * @return	on of {@link RawEncoder}, {@link HextileEncoder}, etc.
	 * 
	 */
	public static EncodingInterface selectEncoder(
			EncodingInterface lastEncoder
			, final int[] clientEncodings
			, final int[] preferredEncodings
			) {
				
		// Reuse previously used encoder, if already set,
		// and VNC client supports it.
		if (lastEncoder != null && 
				containsEncoding(lastEncoder.getType(), clientEncodings) == true) {
			
			return lastEncoder;
		}
		
		// Use RFB service preferred encoding type list.
		if (preferredEncodings != null) {
						
			// Look if some of preferred encodings is present in VNC client supported list.
			for (int encoding : preferredEncodings) {
				
				if (containsEncoding(encoding, clientEncodings) == true) {
					
					lastEncoder = Encodings.newInstance((byte) encoding);
					
					if (lastEncoder == null) {
						
						lastEncoder = new RawEncoder();
					}
					
					log.info(String.format("Selected preferred encoder: '%s'.", lastEncoder.getClass().getSimpleName()));
					
					return lastEncoder;
				}
			}
		}
		
		// Finally, use client list of supported encoding types.
		for (int encoding : clientEncodings) {

			lastEncoder = Encodings.newInstance((byte) encoding);
			
			if (lastEncoder != null) {
				
				log.info(String.format("Selected encoder by client: '%s'.", lastEncoder.getClass().getSimpleName()));
				
				return lastEncoder;
			}
		}
		
		log.info(String.format("Selected fall-back encoder: '%s'.", RawEncoder.class.getSimpleName()));
		
		// Fall-back, raw encoder return. If all of above fails to return result.
		return new RawEncoder();
	}
	
	/**
	 * Method will examine if list of encodings contain given encoding type.
	 * 
	 * @param type			-	desired encoding type to check if its in encoding list
	 * @param encodings		-	encoding list
	 * 
	 * @return	true if encoding type is found in list
	 */
	public static boolean containsEncoding(final int type, final int[] encodings) {
	
		if (encodings == null) {
			
			return false;
		}
		
		for (int encodingType : encodings) {
			
			if (encodingType == type) {
				
				return true;
			}
		}
		
		return false;
	}
}
