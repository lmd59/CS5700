import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author laurend
 * 
 * -class to trust all server certs so that connection can be made to ccs server with unknown CA
 * -simply implements X509TrustManager and does nothing for all checks
 */
public class TrustAllManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// does nothing

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// does nothing

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// does nothing
		return null;
	}

}
