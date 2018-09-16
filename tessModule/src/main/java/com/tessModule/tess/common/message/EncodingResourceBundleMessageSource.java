package com.tessModule.tess.common.message;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.tessModule.tess.common.properties.LoadProperties;

public class EncodingResourceBundleMessageSource extends
		ResourceBundleMessageSource {
	private Logger logger;
	private String defaultEncoding;

	public EncodingResourceBundleMessageSource() {
		this.logger = LoggerFactory
				.getLogger(EncodingResourceBundleMessageSource.class);

		this.defaultEncoding = "UTF-8";
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getMessage(String code, Object[] args) {
		return super.getMessage(code, args, new Locale("ko"));
	}
	
	/**
	 * 매번 배열을 만들기가 귀찮아서... 구분자가 들어있는 문자열로 메서드를 생성한다.
	 * @param code
	 * @param args
	 * @return
	 */
	public String getMessageInternal(String code, String arg, String seporator) {
		String originalMessage = null;
		
		try {
			String[] args = arg.split(seporator);
			originalMessage = super.getMessageInternal(code, args, new Locale(LoadProperties.LOCALE));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return originalMessage;
	}
	
	public String getMessageInternal(String code, String arg) {
		return getMessageInternal(code, arg, ",");
	}
	
	public String getMessageInternal(String code, Object[] args) {
		String originalMessage = super.getMessageInternal(code, args, new Locale(LoadProperties.LOCALE));
		/*if (originalMessage == null)
			return null;
		try {
			return new String(originalMessage.getBytes("8859_1"),
					this.defaultEncoding);
		} catch (UnsupportedEncodingException e) {
			this.logger.error("Unsupported Encoding : {}",
					new Object[] { this.defaultEncoding }, e);
		}*/
		return originalMessage;
	}
	
	protected String getMessageInternal(String code, Object[] args,
			Locale locale) {
		String originalMessage = super.getMessageInternal(code, args, locale);
		if (originalMessage == null)
			return null;
		try {
			return new String(originalMessage.getBytes("8859_1"),
					this.defaultEncoding);
		} catch (UnsupportedEncodingException e) {
			this.logger.error("Unsupported Encoding : {}",
					new Object[] { this.defaultEncoding }, e);
		}
		return null;
	}

	protected String getMessageInternalNotEncoding(String code, Object[] args,
			Locale locale) {
		String originalMessage = super.getMessageInternal(code, args, locale);
		if (originalMessage == null) {
			return null;
		}
		return originalMessage;
	}

	protected Object[] resolveArguments(Object[] args, Locale locale)
  {
    if (args == null) {
      return new Object[0];
    }
    List resolvedArgs = new ArrayList(args.length);
    for (Object arg : args) {
      if (arg instanceof MessageSourceResolvable) {
        resolvedArgs.add(getMessageNotEncoding((MessageSourceResolvable)arg, locale));
      }
      else {
        resolvedArgs.add(arg);
      }
    }
    return resolvedArgs.toArray(new Object[resolvedArgs.size()]);
  }

	protected String getMessageNotEncoding(MessageSourceResolvable resolvable, Locale locale)
    throws NoSuchMessageException
  {
    String[] codes = resolvable.getCodes();
    if (codes == null) {
      codes = new String[0];
    }
    for (String code : codes) {
      String msg = getMessageInternalNotEncoding(code, resolvable.getArguments(), locale);

      if (msg != null) {
        return msg;
      }
    }
    String defaultMessage = resolvable.getDefaultMessage();
    if (defaultMessage != null) {
      return renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
    }

    if (codes.length > 0) {
      String fallback = getDefaultMessage(codes[0]);
      if (fallback != null) {
        return fallback;
      }
    }
    throw new NoSuchMessageException((codes.length > 0) ? codes[(codes.length - 1)] : null, locale);
  }
}