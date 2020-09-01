package in.raster.ioviyam2.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UIDDictionary;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.CommandUtils;
import org.dcm4che2.net.ConfigurationException;
import org.dcm4che2.net.Device;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.DimseRSPHandler;
import org.dcm4che2.net.ExtQueryTransferCapability;
import org.dcm4che2.net.ExtRetrieveTransferCapability;
import org.dcm4che2.net.NetworkApplicationEntity;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.net.NewThreadExecutor;
import org.dcm4che2.net.NoPresentationContextException;
import org.dcm4che2.net.PDVInputStream;
import org.dcm4che2.net.TransferCapability;
import org.dcm4che2.net.UserIdentity;
import org.dcm4che2.net.UserIdentity.Username;
import org.dcm4che2.net.UserIdentity.UsernamePasscode;
import org.dcm4che2.net.service.DicomService;
import org.dcm4che2.net.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcmQR {
	private static final Logger LOG = LoggerFactory.getLogger(DcmQR.class);
	private static final int KB = 1024;
	private static final String USAGE = "dcmqr <aet>[@<host>[:<port>]] [Options]";
	private static final String DESCRIPTION = "Query specified remote Application Entity (=Query/Retrieve SCP) and optional (s. option -cget/-cmove) retrieve instances of matching entities. If <port> is not specified, DICOM default port 104 is assumed. If also no <host> is specified localhost is assumed. Also Storage Services can be provided (s. option -cstore) to receive retrieved instances. For receiving objects retrieved by C-MOVE in a separate association, a local listening port must be specified (s.option -L).\nOptions:";
	private static final String EXAMPLE = "\nExample: dcmqr -L QRSCU:11113 QRSCP@localhost:11112 -cmove QRSCU -qStudyDate=20060204 -qModalitiesInStudy=CT -cstore CT -cstore PR:LE -cstoredest /tmp\n=> Query Application Entity QRSCP listening on local port 11112 for CT studies from Feb 4, 2006 and retrieve matching studies by C-MOVE to own Application Entity QRSCU listing on local port 11113, storing received CT images and Grayscale Softcopy Presentation states to /tmp.";
	private static String[] TLS1 = { "TLSv1" };

	private static String[] SSL3 = { "SSLv3" };

	private static String[] NO_TLS1 = { "SSLv3", "SSLv2Hello" };

	private static String[] NO_SSL2 = { "TLSv1", "SSLv3" };

	private static String[] NO_SSL3 = { "TLSv1", "SSLv2Hello" };

	private static char[] SECRET = { 's', 'e', 'c', 'r', 'e', 't' };

	private static final String[] PATIENT_LEVEL_FIND_CUID = {
			"1.2.840.10008.5.1.4.1.2.1.1", "1.2.840.10008.5.1.4.1.2.3.1" };

	private static final String[] STUDY_LEVEL_FIND_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.1", "1.2.840.10008.5.1.4.1.2.1.1",
			"1.2.840.10008.5.1.4.1.2.3.1" };

	private static final String[] SERIES_LEVEL_FIND_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.1", "1.2.840.10008.5.1.4.1.2.1.1" };

	private static final String[] PATIENT_LEVEL_GET_CUID = {
			"1.2.840.10008.5.1.4.1.2.1.3", "1.2.840.10008.5.1.4.1.2.3.3" };

	private static final String[] STUDY_LEVEL_GET_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.3", "1.2.840.10008.5.1.4.1.2.1.3",
			"1.2.840.10008.5.1.4.1.2.3.3" };

	private static final String[] SERIES_LEVEL_GET_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.3", "1.2.840.10008.5.1.4.1.2.1.3" };

	private static final String[] PATIENT_LEVEL_MOVE_CUID = {
			"1.2.840.10008.5.1.4.1.2.1.2", "1.2.840.10008.5.1.4.1.2.3.2" };

	private static final String[] STUDY_LEVEL_MOVE_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.2", "1.2.840.10008.5.1.4.1.2.1.2",
			"1.2.840.10008.5.1.4.1.2.3.2" };

	private static final String[] SERIES_LEVEL_MOVE_CUID = {
			"1.2.840.10008.5.1.4.1.2.2.2", "1.2.840.10008.5.1.4.1.2.1.2" };

	private static final int[] PATIENT_RETURN_KEYS = { 1048592, 1048608,
			1048624, 1048640, 2101760, 2101762, 2101764 };

	private static final int[] PATIENT_MATCHING_KEYS = { 1048592, 1048608,
			1048609, 1048624, 1048640 };

	private static final int[] STUDY_RETURN_KEYS = { 524320, 524336, 524368,
			2097168, 2097165, 2101766, 2101768 };

	private static final int[] STUDY_MATCHING_KEYS = { 524320, 524336, 524368,
			524385, 524432, 2097168, 2097165 };

	private static final int[] PATIENT_STUDY_MATCHING_KEYS = { 524320, 524336,
			524368, 524385, 524432, 1048592, 1048608, 1048609, 1048624,
			1048640, 2097168, 2097165 };

	private static final int[] SERIES_RETURN_KEYS = { 524384, 2097169, 2097166,
			2101769 };

	private static final int[] SERIES_MATCHING_KEYS = { 524384, 2097169,
			2097166, 4194933 };

	private static final int[] INSTANCE_RETURN_KEYS = { 2097171, 524310, 524312 };

	private static final int[] MOVE_KEYS = { 524370, 1048608, 2097165, 2097166,
			524312 };

	private static final String[] IVRLE_TS = { "1.2.840.10008.1.2" };

	private static final String[] NATIVE_LE_TS = { "1.2.840.10008.1.2.1",
			"1.2.840.10008.1.2" };

	private static final String[] NATIVE_BE_TS = { "1.2.840.10008.1.2.2",
			"1.2.840.10008.1.2" };

	private static final String[] DEFLATED_TS = { "1.2.840.10008.1.2.1.99",
			"1.2.840.10008.1.2.1", "1.2.840.10008.1.2" };

	private static final String[] NOPX_TS = { "1.2.840.10008.1.2.4.96",
			"1.2.840.10008.1.2.1", "1.2.840.10008.1.2" };

	private static final String[] NOPXDEFL_TS = { "1.2.840.10008.1.2.4.97",
			"1.2.840.10008.1.2.4.96", "1.2.840.10008.1.2.1",
			"1.2.840.10008.1.2" };

	private static final String[] JPLL_TS = { "1.2.840.10008.1.2.4.70",
			"1.2.840.10008.1.2.4.57", "1.2.840.10008.1.2.4.80",
			"1.2.840.10008.1.2.4.90", "1.2.840.10008.1.2.1",
			"1.2.840.10008.1.2" };

	private static final String[] JPLY_TS = { "1.2.840.10008.1.2.4.50",
			"1.2.840.10008.1.2.4.51", "1.2.840.10008.1.2.4.81",
			"1.2.840.10008.1.2.4.91", "1.2.840.10008.1.2.1",
			"1.2.840.10008.1.2" };

	private static final String[] MPEG2_TS = { "1.2.840.10008.1.2.4.100" };

	private static final String[] DEF_TS = { "1.2.840.10008.1.2.4.70",
			"1.2.840.10008.1.2.4.57", "1.2.840.10008.1.2.4.80",
			"1.2.840.10008.1.2.4.81", "1.2.840.10008.1.2.4.90",
			"1.2.840.10008.1.2.4.91", "1.2.840.10008.1.2.4.50",
			"1.2.840.10008.1.2.4.51", "1.2.840.10008.1.2.4.100",
			"1.2.840.10008.1.2.1.99", "1.2.840.10008.1.2.2",
			"1.2.840.10008.1.2.1", "1.2.840.10008.1.2" };

	private static final String[] EMPTY_STRING = new String[0];
	private final Executor executor;
	private final NetworkApplicationEntity remoteAE = new NetworkApplicationEntity();

	private final NetworkConnection remoteConn = new NetworkConnection();
	private final Device device;
	private final NetworkApplicationEntity ae = new NetworkApplicationEntity();

	private final NetworkConnection conn = new NetworkConnection();
	private Association assoc;
	private int priority = 0;
	private boolean cfind;
	private boolean cget;
	private String moveDest;
	private File storeDest;
	private boolean devnull;
	private int fileBufferSize = 256;

	private boolean evalRetrieveAET = false;

	private QueryRetrieveLevel qrlevel = QueryRetrieveLevel.STUDY;

	private List<String> privateFind = new ArrayList();

	private final List<TransferCapability> storeTransferCapability = new ArrayList(
			8);

	private DicomObject keys = new BasicDicomObject();

	private int cancelAfter = 2147483647;
	private int completed;
	private int warning;
	private int failed;
	private boolean relationQR;
	private boolean dateTimeMatching;
	private boolean fuzzySemanticPersonNameMatching;
	private boolean noExtNegotiation;
	private String keyStoreURL = "resource:tls/test_sys_1.p12";

	private char[] keyStorePassword = SECRET;
	private char[] keyPassword;
	private String trustStoreURL = "resource:tls/mesa_certs.jks";

	private char[] trustStorePassword = SECRET;

	private final DimseRSPHandler rspHandler = new DimseRSPHandler() {
		public void onDimseRSP(Association as, DicomObject cmd, DicomObject data) {
			DcmQR.this.onMoveRSP(as, cmd, data);
		}
	};

	public DcmQR(String name) {
		this.device = new Device(name);
		this.executor = new NewThreadExecutor(name);
		this.remoteAE.setInstalled(true);
		this.remoteAE.setAssociationAcceptor(true);
		this.remoteAE
				.setNetworkConnection(new NetworkConnection[] { this.remoteConn });

		this.device.setNetworkApplicationEntity(this.ae);
		this.device.setNetworkConnection(this.conn);
		this.ae.setNetworkConnection(this.conn);
		this.ae.setAssociationInitiator(true);
		this.ae.setAssociationAcceptor(true);
		this.ae.setAETitle(name);
	}

	public final void setLocalHost(String hostname) {
		this.conn.setHostname(hostname);
	}

	public final void setLocalPort(int port) {
		this.conn.setPort(port);
	}

	public final void setRemoteHost(String hostname) {
		this.remoteConn.setHostname(hostname);
	}

	public final void setRemotePort(int port) {
		this.remoteConn.setPort(port);
	}

	public final void setTlsProtocol(String[] tlsProtocol) {
		this.conn.setTlsProtocol(tlsProtocol);
	}

	public final void setTlsWithoutEncyrption() {
		this.conn.setTlsWithoutEncyrption();
		this.remoteConn.setTlsWithoutEncyrption();
	}

	public final void setTls3DES_EDE_CBC() {
		this.conn.setTls3DES_EDE_CBC();
		this.remoteConn.setTls3DES_EDE_CBC();
	}

	public final void setTlsAES_128_CBC() {
		this.conn.setTlsAES_128_CBC();
		this.remoteConn.setTlsAES_128_CBC();
	}

	public final void setTlsNeedClientAuth(boolean needClientAuth) {
		this.conn.setTlsNeedClientAuth(needClientAuth);
	}

	public final void setKeyStoreURL(String url) {
		this.keyStoreURL = url;
	}

	public final void setKeyStorePassword(String pw) {
		this.keyStorePassword = pw.toCharArray();
	}

	public final void setKeyPassword(String pw) {
		this.keyPassword = pw.toCharArray();
	}

	public final void setTrustStorePassword(String pw) {
		this.trustStorePassword = pw.toCharArray();
	}

	public final void setTrustStoreURL(String url) {
		this.trustStoreURL = url;
	}

	public final void setCalledAET(String called, boolean reuse) {
		this.remoteAE.setAETitle(called);
		if (reuse)
			this.ae.setReuseAssocationToAETitle(new String[] { called });
	}

	public final void setCalling(String calling) {
		this.ae.setAETitle(calling);
	}

	public final void setUserIdentity(UserIdentity userIdentity) {
		this.ae.setUserIdentity(userIdentity);
	}

	public final void setPriority(int priority) {
		this.priority = priority;
	}

	public final void setConnectTimeout(int connectTimeout) {
		this.conn.setConnectTimeout(connectTimeout);
	}

	public final void setMaxPDULengthReceive(int maxPDULength) {
		this.ae.setMaxPDULengthReceive(maxPDULength);
	}

	public final void setMaxOpsInvoked(int maxOpsInvoked) {
		this.ae.setMaxOpsInvoked(maxOpsInvoked);
	}

	public final void setMaxOpsPerformed(int maxOps) {
		this.ae.setMaxOpsPerformed(maxOps);
	}

	public final void setPackPDV(boolean packPDV) {
		this.ae.setPackPDV(packPDV);
	}

	public final void setAssociationReaperPeriod(int period) {
		this.device.setAssociationReaperPeriod(period);
	}

	public final void setDimseRspTimeout(int timeout) {
		this.ae.setDimseRspTimeout(timeout);
	}

	public final void setRetrieveRspTimeout(int timeout) {
		this.ae.setRetrieveRspTimeout(timeout);
	}

	public final void setTcpNoDelay(boolean tcpNoDelay) {
		this.conn.setTcpNoDelay(tcpNoDelay);
	}

	public final void setAcceptTimeout(int timeout) {
		this.conn.setAcceptTimeout(timeout);
	}

	public final void setReleaseTimeout(int timeout) {
		this.conn.setReleaseTimeout(timeout);
	}

	public final void setSocketCloseDelay(int timeout) {
		this.conn.setSocketCloseDelay(timeout);
	}

	public final void setMaxPDULengthSend(int maxPDULength) {
		this.ae.setMaxPDULengthSend(maxPDULength);
	}

	public final void setReceiveBufferSize(int bufferSize) {
		this.conn.setReceiveBufferSize(bufferSize);
	}

	public final void setSendBufferSize(int bufferSize) {
		this.conn.setSendBufferSize(bufferSize);
	}

	public final void setFileBufferSize(int size) {
		this.fileBufferSize = size;
	}

	private static CommandLine parse(String[] args) {
		Options opts = new Options();

		OptionBuilder.withArgName("name");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("set device name, use DCMQR by default");

		opts.addOption(OptionBuilder.create("device"));

		OptionBuilder.withArgName("aet[@host][:port]");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("set AET, local address and listening port of localApplication Entity, use device name and pick up any valid local address to bind the socket by default");

		opts.addOption(OptionBuilder.create("L"));

		OptionBuilder.withArgName("username");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("enable User Identity Negotiation with specified username and  optional passcode");

		opts.addOption(OptionBuilder.create("username"));

		OptionBuilder.withArgName("passcode");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("optional passcode for User Identity Negotiation, only effective with option -username");

		opts.addOption(OptionBuilder.create("passcode"));

		opts.addOption(
				"uidnegrsp",
				false,
				"request positive User Identity Negotation response, only effective with option -username");

		OptionBuilder.withArgName("NULL|3DES|AES");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("enable TLS connection without, 3DES or AES encryption");

		opts.addOption(OptionBuilder.create("tls"));

		OptionGroup tlsProtocol = new OptionGroup();
		tlsProtocol.addOption(new Option("tls1",
				"disable the use of SSLv3 and SSLv2 for TLS connections"));

		tlsProtocol.addOption(new Option("ssl3",
				"disable the use of TLSv1 and SSLv2 for TLS connections"));

		tlsProtocol.addOption(new Option("no_tls1",
				"disable the use of TLSv1 for TLS connections"));

		tlsProtocol.addOption(new Option("no_ssl3",
				"disable the use of SSLv3 for TLS connections"));

		tlsProtocol.addOption(new Option("no_ssl2",
				"disable the use of SSLv2 for TLS connections"));

		opts.addOptionGroup(tlsProtocol);

		opts.addOption("noclientauth", false,
				"disable client authentification for TLS");

		OptionBuilder.withArgName("file|url");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("file path or URL of P12 or JKS keystore, resource:tls/test_sys_1.p12 by default");

		opts.addOption(OptionBuilder.create("keystore"));

		OptionBuilder.withArgName("password");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("password for keystore file, 'secret' by default");

		opts.addOption(OptionBuilder.create("keystorepw"));

		OptionBuilder.withArgName("password");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("password for accessing the key in the keystore, keystore password by default");

		opts.addOption(OptionBuilder.create("keypw"));

		OptionBuilder.withArgName("file|url");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("file path or URL of JKS truststore, resource:tls/mesa_certs.jks by default");

		opts.addOption(OptionBuilder.create("truststore"));

		OptionBuilder.withArgName("password");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("password for truststore file, 'secret' by default");

		opts.addOption(OptionBuilder.create("truststorepw"));

		OptionBuilder.withArgName("aet");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("retrieve instances of matching entities by C-MOVE to specified destination.");

		opts.addOption(OptionBuilder.create("cmove"));

		opts.addOption(
				"nocfind",
				false,
				"retrieve instances without previous query - unique keys must be specified by -q options");

		opts.addOption("cget", false,
				"retrieve instances of matching entities by C-GET.");

		OptionBuilder.withArgName("cuid[:ts]");
		OptionBuilder.hasArgs();
		OptionBuilder
				.withDescription("negotiate support of specified Storage SOP Class and Transfer Syntaxes. The Storage SOP Class may be specified by its UID or by one of following key words:\nCR  - Computed Radiography Image Storage\nCT  - CT Image Storage\nMR  - MRImageStorage\nUS  - Ultrasound Image Storage\nNM  - Nuclear Medicine Image Storage\nPET - PET Image Storage\nSC  - Secondary Capture Image Storage\nXA  - XRay Angiographic Image Storage\nXRF - XRay Radiofluoroscopic Image Storage\nDX  - Digital X-Ray Image Storage for Presentation\nMG  - Digital Mammography X-Ray Image Storage for Presentation\nPR  - Grayscale Softcopy Presentation State Storage\nKO  - Key Object Selection Document Storage\nSR  - Basic Text Structured Report Document Storage\nThe Transfer Syntaxes may be specified by a comma separated list of UIDs or by one of following key words:\nIVRLE - offer only Implicit VR Little Endian Transfer Syntax\nLE - offer Explicit and Implicit VR Little Endian Transfer Syntax\nBE - offer Explicit VR Big Endian Transfer Syntax\nDEFL - offer Deflated Explicit VR Little Endian Transfer Syntax\nJPLL - offer JEPG Loss Less Transfer Syntaxes\nJPLY - offer JEPG Lossy Transfer Syntaxes\nMPEG2 - offer MPEG2 Transfer Syntax\nNOPX - offer No Pixel Data Transfer Syntax\nNOPXD - offer No Pixel Data Deflate Transfer Syntax\nIf only the Storage SOP Class is specified, all Transfer Syntaxes listed above except No Pixel Data and No Pixel Data Delflate Transfer Syntax are offered.");

		opts.addOption(OptionBuilder.create("cstore"));

		OptionBuilder.withArgName("dir");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("store received objects into files in specified directory <dir>. Do not store received objects by default.");

		opts.addOption(OptionBuilder.create("cstoredest"));

		opts.addOption("ivrle", false,
				"offer only Implicit VR Little Endian Transfer Syntax.");

		OptionBuilder.withArgName("maxops");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("maximum number of outstanding C-MOVE-RQ it may invoke asynchronously, 1 by default.");

		opts.addOption(OptionBuilder.create("async"));

		OptionBuilder.withArgName("maxops");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("maximum number of outstanding storage operations performed asynchronously, unlimited by default.");

		opts.addOption(OptionBuilder.create("storeasync"));

		opts.addOption("noextneg", false, "disable extended negotiation.");
		opts.addOption("rel", false,
				"negotiate support of relational queries and retrieval.");

		opts.addOption("datetime", false,
				"negotiate support of combined date and time attribute range matching.");

		opts.addOption("fuzzy", false,
				"negotiate support of fuzzy semantic person name attribute matching.");

		opts.addOption(
				"retall",
				false,
				"negotiate private FIND SOP Classes to fetch all available attributes of matching entities.");

		opts.addOption(
				"blocked",
				false,
				"negotiate private FIND SOP Classes to return attributes of several matching entities per FIND response.");

		opts.addOption(
				"vmf",
				false,
				"negotiate private FIND SOP Classes to return attributes of legacy CT/MR images of one series as virtual multiframe object.");

		opts.addOption(
				"pdv1",
				false,
				"send only one PDV in one P-Data-TF PDU, pack command and data PDV in one P-DATA-TF PDU by default.");

		opts.addOption("tcpdelay", false,
				"set TCP_NODELAY socket option to false, true by default");

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for TCP connect, no timeout by default");

		opts.addOption(OptionBuilder.create("connectTO"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("delay in ms for Socket close after sending A-ABORT, 50ms by default");

		opts.addOption(OptionBuilder.create("soclosedelay"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("period in ms to check for outstanding DIMSE-RSP, 10s by default");

		opts.addOption(OptionBuilder.create("reaper"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for receiving C-FIND-RSP, 60s by default");

		opts.addOption(OptionBuilder.create("cfindrspTO"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for receiving C-MOVE-RSP and C-GET RSP, 600s by default");

		opts.addOption(OptionBuilder.create("cmoverspTO"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for receiving C-GET-RSP and C-MOVE RSP, 600s by default");

		opts.addOption(OptionBuilder.create("cgetrspTO"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for receiving A-ASSOCIATE-AC, 5s by default");

		opts.addOption(OptionBuilder.create("acceptTO"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("timeout in ms for receiving A-RELEASE-RP, 5s by default");

		opts.addOption(OptionBuilder.create("releaseTO"));

		OptionBuilder.withArgName("KB");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("maximal length in KB of received P-DATA-TF PDUs, 16KB by default");

		opts.addOption(OptionBuilder.create("rcvpdulen"));

		OptionBuilder.withArgName("KB");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("maximal length in KB of sent P-DATA-TF PDUs, 16KB by default");

		opts.addOption(OptionBuilder.create("sndpdulen"));

		OptionBuilder.withArgName("KB");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("set SO_RCVBUF socket option to specified value in KB");

		opts.addOption(OptionBuilder.create("sorcvbuf"));

		OptionBuilder.withArgName("KB");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("set SO_SNDBUF socket option to specified value in KB");

		opts.addOption(OptionBuilder.create("sosndbuf"));

		OptionBuilder.withArgName("KB");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("minimal buffer size to write received object to file, 1KB by default");

		opts.addOption(OptionBuilder.create("filebuf"));

		OptionGroup qrlevel = new OptionGroup();

		OptionBuilder
				.withDescription("perform patient level query, multiple exclusive with -S and -I, perform study level query by default.");

		OptionBuilder.withLongOpt("patient");
		qrlevel.addOption(OptionBuilder.create("P"));

		OptionBuilder
				.withDescription("perform series level query, multiple exclusive with -P and -I, perform study level query by default.");

		OptionBuilder.withLongOpt("series");
		qrlevel.addOption(OptionBuilder.create("S"));

		OptionBuilder
				.withDescription("perform instance level query, multiple exclusive with -P and -S, perform study level query by default.");

		OptionBuilder.withLongOpt("image");
		qrlevel.addOption(OptionBuilder.create("I"));

		OptionBuilder.withArgName("cuid");
		OptionBuilder.hasArgs();
		OptionBuilder
				.withDescription("negotiate addition private C-FIND SOP class with specified UID");

		opts.addOption(OptionBuilder.create("cfind"));

		opts.addOptionGroup(qrlevel);

		OptionBuilder.withArgName("[seq/]attr=value");
		OptionBuilder.hasArgs();
		OptionBuilder.withValueSeparator('=');
		OptionBuilder
				.withDescription("specify matching key. attr can be specified by name or tag value (in hex), e.g. PatientName or 00100010. Attributes in nested Datasets can be specified by including the name/tag value of the sequence attribute, e.g. 00400275/00400009 for Scheduled Procedure Step ID in the Request Attributes Sequence");

		opts.addOption(OptionBuilder.create("q"));

		OptionBuilder.withArgName("attr");
		OptionBuilder.hasArgs();
		OptionBuilder
				.withDescription("specify additional return key. attr can be specified by name or tag value (in hex).");

		opts.addOption(OptionBuilder.create("r"));

		opts.addOption("nodefret", false,
				"only inlcude return keys specified by -r into the request.");

		OptionBuilder.withArgName("num");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("cancel query after receive of specified number of responses, no cancel by default");

		opts.addOption(OptionBuilder.create("C"));

		OptionBuilder.withArgName("aet");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("retrieve matching objects to specified move destination.");

		opts.addOption(OptionBuilder.create("cmove"));

		opts.addOption("evalRetrieveAET", false,
				"Only Move studies not allready stored on destination AET");

		opts.addOption("lowprior", false,
				"LOW priority of the C-FIND/C-MOVE operation, MEDIUM by default");

		opts.addOption("highprior", false,
				"HIGH priority of the C-FIND/C-MOVE operation, MEDIUM by default");

		OptionBuilder.withArgName("num");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("repeat query (and retrieve) several times");
		opts.addOption(OptionBuilder.create("repeat"));

		OptionBuilder.withArgName("ms");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("delay in ms between repeated query (and retrieve), no delay by default");

		opts.addOption(OptionBuilder.create("repeatdelay"));

		opts.addOption("reuseassoc", false,
				"Reuse association for repeated query (and retrieve)");

		opts.addOption("closeassoc", false,
				"Close association between repeated query (and retrieve)");

		opts.addOption("h", "help", false, "print this message");
		opts.addOption("V", "version", false,
				"print the version information and exit");

		CommandLine cl = null;
		try {
			cl = new GnuParser().parse(opts, args);
		} catch (ParseException e) {
			exit("dcmqr: " + e.getMessage());
			throw new RuntimeException("unreachable");
		}
		if (cl.hasOption('V')) {
			Package p = DcmQR.class.getPackage();
			System.out.println("dcmqr v" + p.getImplementationVersion());
		}

		if ((cl.hasOption('h')) || (cl.getArgList().size() != 1)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter
					.printHelp(
							"dcmqr <aet>[@<host>[:<port>]] [Options]",
							"Query specified remote Application Entity (=Query/Retrieve SCP) and optional (s. option -cget/-cmove) retrieve instances of matching entities. If <port> is not specified, DICOM default port 104 is assumed. If also no <host> is specified localhost is assumed. Also Storage Services can be provided (s. option -cstore) to receive retrieved instances. For receiving objects retrieved by C-MOVE in a separate association, a local listening port must be specified (s.option -L).\nOptions:",
							opts,
							"\nExample: dcmqr -L QRSCU:11113 QRSCP@localhost:11112 -cmove QRSCU -qStudyDate=20060204 -qModalitiesInStudy=CT -cstore CT -cstore PR:LE -cstoredest /tmp\n=> Query Application Entity QRSCP listening on local port 11112 for CT studies from Feb 4, 2006 and retrieve matching studies by C-MOVE to own Application Entity QRSCU listing on local port 11113, storing received CT images and Grayscale Softcopy Presentation states to /tmp.");
		}

		return cl;
	}

	public List<DicomObject> QueryDcm(String[] args) {
		ArrayList listdicom = new ArrayList();
		CommandLine cl = parse(args);
		DcmQR dcmqr = new DcmQR(
				cl.hasOption("device") ? cl.getOptionValue("device") : "DCMQR");

		List argList = cl.getArgList();		
		String remoteAE = (String) argList.get(0);//null@null:null
		String[] calledAETAddress = split(remoteAE, '@');
		dcmqr.setCalledAET(calledAETAddress[0], cl.hasOption("reuseassoc"));
		if (calledAETAddress[1] == null) {
			dcmqr.setRemoteHost("127.0.0.1");
			dcmqr.setRemotePort(104);
		} else {
			String[] hostPort = split(calledAETAddress[1], ':');
			dcmqr.setRemoteHost(hostPort[0]);		
			dcmqr.setRemotePort(toPort(hostPort[1]));
		}
		if (cl.hasOption("L")) {
			String localAE = cl.getOptionValue("L");
			String[] localPort = split(localAE, ':');
			if (localPort[1] != null) {
				dcmqr.setLocalPort(toPort(localPort[1]));
			}
			String[] callingAETHost = split(localPort[0], '@');
			dcmqr.setCalling(callingAETHost[0]);
			if (callingAETHost[1] != null) {
				dcmqr.setLocalHost(callingAETHost[1]);
			}
		}
		if (cl.hasOption("username")) {
			String username = cl.getOptionValue("username");
			UserIdentity userId;
			if (cl.hasOption("passcode")) {
				String passcode = cl.getOptionValue("passcode");
				userId = new UserIdentity.UsernamePasscode(username,
						passcode.toCharArray());
			} else {
				userId = new UserIdentity.Username(username);
			}
			userId.setPositiveResponseRequested(cl.hasOption("uidnegrsp"));
			dcmqr.setUserIdentity(userId);
		}
		if (cl.hasOption("connectTO")) {
			dcmqr.setConnectTimeout(parseInt(cl.getOptionValue("connectTO"),
					"illegal argument of option -connectTO", 1, 2147483647));
		}

		if (cl.hasOption("reaper")) {
			dcmqr.setAssociationReaperPeriod(parseInt(
					cl.getOptionValue("reaper"),
					"illegal argument of option -reaper", 1, 2147483647));
		}

		if (cl.hasOption("cfindrspTO")) {
			dcmqr.setDimseRspTimeout(parseInt(cl.getOptionValue("cfindrspTO"),
					"illegal argument of option -cfindrspTO", 1, 2147483647));
		}
		if (cl.hasOption("cmoverspTO")) {
			dcmqr.setRetrieveRspTimeout(parseInt(
					cl.getOptionValue("cmoverspTO"),
					"illegal argument of option -cmoverspTO", 1, 2147483647));
		}
		if (cl.hasOption("cgetrspTO")) {
			dcmqr.setRetrieveRspTimeout(parseInt(
					cl.getOptionValue("cgetrspTO"),
					"illegal argument of option -cgetrspTO", 1, 2147483647));
		}
		if (cl.hasOption("acceptTO")) {
			dcmqr.setAcceptTimeout(parseInt(cl.getOptionValue("acceptTO"),
					"illegal argument of option -acceptTO", 1, 2147483647));
		}

		if (cl.hasOption("releaseTO")) {
			dcmqr.setReleaseTimeout(parseInt(cl.getOptionValue("releaseTO"),
					"illegal argument of option -releaseTO", 1, 2147483647));
		}

		if (cl.hasOption("soclosedelay")) {
			dcmqr.setSocketCloseDelay(parseInt(
					cl.getOptionValue("soclosedelay"),
					"illegal argument of option -soclosedelay", 1, 10000));
		}

		if (cl.hasOption("rcvpdulen")) {
			dcmqr.setMaxPDULengthReceive(parseInt(
					cl.getOptionValue("rcvpdulen"),
					"illegal argument of option -rcvpdulen", 1, 10000) * 1024);
		}

		if (cl.hasOption("sndpdulen")) {
			dcmqr.setMaxPDULengthSend(parseInt(cl.getOptionValue("sndpdulen"),
					"illegal argument of option -sndpdulen", 1, 10000) * 1024);
		}

		if (cl.hasOption("sosndbuf")) {
			dcmqr.setSendBufferSize(parseInt(cl.getOptionValue("sosndbuf"),
					"illegal argument of option -sosndbuf", 1, 10000) * 1024);
		}

		if (cl.hasOption("sorcvbuf")) {
			dcmqr.setReceiveBufferSize(parseInt(cl.getOptionValue("sorcvbuf"),
					"illegal argument of option -sorcvbuf", 1, 10000) * 1024);
		}

		if (cl.hasOption("filebuf")) {
			dcmqr.setFileBufferSize(parseInt(cl.getOptionValue("filebuf"),
					"illegal argument of option -filebuf", 1, 10000) * 1024);
		}

		dcmqr.setPackPDV(!cl.hasOption("pdv1"));
		dcmqr.setTcpNoDelay(!cl.hasOption("tcpdelay"));
		dcmqr.setMaxOpsInvoked(cl.hasOption("async") ? parseInt(
				cl.getOptionValue("async"),
				"illegal argument of option -async", 0, 65535) : 1);

		dcmqr.setMaxOpsPerformed(cl.hasOption("cstoreasync") ? parseInt(
				cl.getOptionValue("cstoreasync"),
				"illegal argument of option -cstoreasync", 0, 65535) : 0);

		if (cl.hasOption("C")) {
			dcmqr.setCancelAfter(parseInt(cl.getOptionValue("C"),
					"illegal argument of option -C", 1, 2147483647));
		}
		if (cl.hasOption("lowprior"))
			dcmqr.setPriority(2);
		if (cl.hasOption("highprior"))
			dcmqr.setPriority(1);
		int colon;
		if (cl.hasOption("cstore")) {
			String[] storeTCs = cl.getOptionValues("cstore");
			for (String storeTC : storeTCs) {
				colon = storeTC.indexOf(58);
				String[] tsuids;
				String cuid;
				if (colon == -1) {
					cuid = storeTC;
					tsuids = DEF_TS;
				} else {
					cuid = storeTC.substring(0, colon);
					String ts = storeTC.substring(colon + 1);
					try {
						tsuids = TS.valueOf(ts).uids;
					} catch (IllegalArgumentException e) {
						tsuids = ts.split(",");
					}
				}
				try {
					cuid = CUID.valueOf(cuid).uid;
				} catch (IllegalArgumentException e) {
				}
				dcmqr.addStoreTransferCapability(cuid, tsuids);
			}
			if (cl.hasOption("cstoredest"))
				dcmqr.setStoreDestination(cl.getOptionValue("cstoredest"));
		}
		dcmqr.setCFind(!cl.hasOption("nocfind"));
		dcmqr.setCGet(cl.hasOption("cget"));
		if (cl.hasOption("cmove"))
			dcmqr.setMoveDest(cl.getOptionValue("cmove"));
		if (cl.hasOption("evalRetrieveAET"))
			dcmqr.setEvalRetrieveAET(true);
		dcmqr.setQueryLevel(cl.hasOption("I") ? QueryRetrieveLevel.IMAGE : cl
				.hasOption("S") ? QueryRetrieveLevel.SERIES
				: cl.hasOption("P") ? QueryRetrieveLevel.PATIENT
						: QueryRetrieveLevel.STUDY);

		if (cl.hasOption("noextneg"))
			dcmqr.setNoExtNegotiation(true);
		if (cl.hasOption("rel"))
			dcmqr.setRelationQR(true);
		if (cl.hasOption("datetime"))
			dcmqr.setDateTimeMatching(true);
		if (cl.hasOption("fuzzy"))
			dcmqr.setFuzzySemanticPersonNameMatching(true);
		if (!cl.hasOption("P")) {
			if (cl.hasOption("retall")) {
				dcmqr.addPrivate("1.2.40.0.13.1.5.1.4.1.2.2.1");
			}
			if (cl.hasOption("blocked")) {
				dcmqr.addPrivate("1.2.40.0.13.1.5.1.4.1.2.2.1.1");
			}
			if (cl.hasOption("vmf")) {
				dcmqr.addPrivate("1.2.40.0.13.1.5.1.4.1.2.2.1.2");
			}
		}
		if (cl.hasOption("cfind")) {
			String[] cuids = cl.getOptionValues("cfind");
			for (int i = 0; i < cuids.length; i++)
				dcmqr.addPrivate(cuids[i]);
		}
		if (!cl.hasOption("nodefret"))
			dcmqr.addDefReturnKeys();
		if (cl.hasOption("r")) {
			String[] returnKeys = cl.getOptionValues("r");
			for (int i = 0; i < returnKeys.length; i++)
				dcmqr.addReturnKey(Tag.toTagPath(returnKeys[i]));
		}
		if (cl.hasOption("q")) {
			String[] matchingKeys = cl.getOptionValues("q");
			for (int i = 1; i < matchingKeys.length; i++) {
				dcmqr.addMatchingKey(Tag.toTagPath(matchingKeys[(i - 1)]),
						matchingKeys[i]);

				i++;
			}
		}

		dcmqr.configureTransferCapability(cl.hasOption("ivrle"));

		int repeat = cl.hasOption("repeat") ? parseInt(
				cl.getOptionValue("repeat"),
				"illegal argument of option -repeat", 1, 2147483647) : 0;

		int interval = cl.hasOption("repeatdelay") ? parseInt(
				cl.getOptionValue("repeatdelay"),
				"illegal argument of option -repeatdelay", 1, 2147483647) : 0;

		boolean closeAssoc = cl.hasOption("closeassoc");
		long t2;
		if (cl.hasOption("tls")) {
			String cipher = cl.getOptionValue("tls");
			if ("NULL".equalsIgnoreCase(cipher))
				dcmqr.setTlsWithoutEncyrption();
			else if ("3DES".equalsIgnoreCase(cipher))
				dcmqr.setTls3DES_EDE_CBC();
			else if ("AES".equalsIgnoreCase(cipher))
				dcmqr.setTlsAES_128_CBC();
			else {
				exit("Invalid parameter for option -tls: " + cipher);
			}
			if (cl.hasOption("tls1"))
				dcmqr.setTlsProtocol(TLS1);
			else if (cl.hasOption("ssl3"))
				dcmqr.setTlsProtocol(SSL3);
			else if (cl.hasOption("no_tls1"))
				dcmqr.setTlsProtocol(NO_TLS1);
			else if (cl.hasOption("no_ssl3"))
				dcmqr.setTlsProtocol(NO_SSL3);
			else if (cl.hasOption("no_ssl2")) {
				dcmqr.setTlsProtocol(NO_SSL2);
			}
			dcmqr.setTlsNeedClientAuth(!cl.hasOption("noclientauth"));
			if (cl.hasOption("keystore")) {
				dcmqr.setKeyStoreURL(cl.getOptionValue("keystore"));
			}
			if (cl.hasOption("keystorepw")) {
				dcmqr.setKeyStorePassword(cl.getOptionValue("keystorepw"));
			}

			if (cl.hasOption("keypw")) {
				dcmqr.setKeyPassword(cl.getOptionValue("keypw"));
			}
			if (cl.hasOption("truststore")) {
				dcmqr.setTrustStoreURL(cl.getOptionValue("truststore"));
			}

			if (cl.hasOption("truststorepw")) {
				dcmqr.setTrustStorePassword(cl.getOptionValue("truststorepw"));
			}

			long t1 = System.currentTimeMillis();
			try {
				dcmqr.initTLS();
			} catch (Exception e) {
				System.err.println("ERROR: Failed to initialize TLS context:"
						+ e.getMessage());
			}

			t2 = System.currentTimeMillis();
		}
		try {
			dcmqr.start();
		} catch (Exception e) {
			System.err
					.println("ERROR: Failed to start server for receiving requested objects:"
							+ e.getMessage());
		}

		try {
			long t1 = System.currentTimeMillis();
			try {
				dcmqr.open();
			} catch (Exception e) {
				LOG.error("Failed to establish association:", e);
			}

			t2 = System.currentTimeMillis();
			while (true) {
				List<DicomObject> result;
				if (dcmqr.isCFind()) {
					listdicom = (ArrayList) dcmqr.query();

					long t3 = System.currentTimeMillis();

					t2 = t3;
				} else {
					result = Collections.singletonList(dcmqr.getKeys());
				}
				long t4;
				if ((dcmqr.isCMove()) || (dcmqr.isCGet())) {
					if (dcmqr.isCMove())
						dcmqr.move(listdicom);
					else
						dcmqr.get(listdicom);
					t4 = System.currentTimeMillis();
				}

				if ((repeat == 0) || (closeAssoc)) {
					try {
						dcmqr.close();
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}

				if (repeat-- == 0)
					break;
				Thread.sleep(interval);
				t4 = System.currentTimeMillis();
				dcmqr.open();
				t2 = System.currentTimeMillis();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			dcmqr.stop();
		}
		return listdicom;
	}

	public void addStoreTransferCapability(String cuid, String[] tsuids) {
		this.storeTransferCapability.add(new TransferCapability(cuid, tsuids,
				"SCP"));
	}

	public void setEvalRetrieveAET(boolean evalRetrieveAET) {
		this.evalRetrieveAET = evalRetrieveAET;
	}

	public boolean isEvalRetrieveAET() {
		return this.evalRetrieveAET;
	}

	public void setNoExtNegotiation(boolean b) {
		this.noExtNegotiation = b;
	}

	public void setFuzzySemanticPersonNameMatching(boolean b) {
		this.fuzzySemanticPersonNameMatching = b;
	}

	public void setDateTimeMatching(boolean b) {
		this.dateTimeMatching = b;
	}

	public void setRelationQR(boolean b) {
		this.relationQR = b;
	}

	public final int getFailed() {
		return this.failed;
	}

	public final int getWarning() {
		return this.warning;
	}

	public final int getTotalRetrieved() {
		return this.completed + this.warning;
	}

	public void setCancelAfter(int limit) {
		this.cancelAfter = limit;
	}

	public void addMatchingKey(int[] tagPath, String value) {
		this.keys.putString(tagPath, null, value);
	}

	public void addReturnKey(int[] tagPath) {
		this.keys.putNull(tagPath, null);
	}

	public void addDefReturnKeys() {
		for (int tag : this.qrlevel.getReturnKeys())
			this.keys.putNull(tag, null);
	}

	public void configureTransferCapability(boolean ivrle) {
		String[] findcuids = this.qrlevel.getFindClassUids();
		String[] movecuids = this.moveDest != null ? this.qrlevel
				.getMoveClassUids() : EMPTY_STRING;

		String[] getcuids = this.cget ? this.qrlevel.getGetClassUids()
				: EMPTY_STRING;

		TransferCapability[] tcs = new TransferCapability[findcuids.length
				+ this.privateFind.size() + movecuids.length + getcuids.length
				+ this.storeTransferCapability.size()];

		int i = 0;
		for (String cuid : findcuids)
			tcs[(i++)] = mkFindTC(cuid, ivrle ? IVRLE_TS : NATIVE_LE_TS);
		for (String cuid : this.privateFind)
			tcs[(i++)] = mkFindTC(cuid, ivrle ? IVRLE_TS : DEFLATED_TS);
		for (String cuid : movecuids)
			tcs[(i++)] = mkRetrieveTC(cuid, ivrle ? IVRLE_TS : NATIVE_LE_TS);
		for (String cuid : getcuids)
			tcs[(i++)] = mkRetrieveTC(cuid, ivrle ? IVRLE_TS : NATIVE_LE_TS);
		for (TransferCapability tc : this.storeTransferCapability) {
			tcs[(i++)] = tc;
		}
		this.ae.setTransferCapability(tcs);
		if (!this.storeTransferCapability.isEmpty())
			this.ae.register(createStorageService());
	}

	private DicomService createStorageService() {
		String[] cuids = new String[this.storeTransferCapability.size()];
		int i = 0;
		for (TransferCapability tc : this.storeTransferCapability) {
			cuids[(i++)] = tc.getSopClass();
		}
		return new StorageService(cuids) {
			protected void onCStoreRQ(Association as, int pcid, DicomObject rq,
					PDVInputStream dataStream, String tsuid, DicomObject rsp)
					throws IOException, DicomServiceException {
				if (DcmQR.this.storeDest == null)
					super.onCStoreRQ(as, pcid, rq, dataStream, tsuid, rsp);
				else
					try {
						String cuid = rq.getString(2);
						String iuid = rq.getString(4096);
						BasicDicomObject fmi = new BasicDicomObject();
						fmi.initFileMetaInformation(cuid, iuid, tsuid);
						File file = DcmQR.this.devnull ? DcmQR.this.storeDest
								: new File(DcmQR.this.storeDest, iuid);
						FileOutputStream fos = new FileOutputStream(file);
						BufferedOutputStream bos = new BufferedOutputStream(
								fos, DcmQR.this.fileBufferSize);

						DicomOutputStream dos = new DicomOutputStream(bos);
						dos.writeFileMetaInformation(fmi);
						dataStream.copyTo(dos);
						dos.close();
					} catch (IOException e) {
						throw new DicomServiceException(rq, 272, e.getMessage());
					}
			}
		};
	}

	private TransferCapability mkRetrieveTC(String cuid, String[] ts) {
		ExtRetrieveTransferCapability tc = new ExtRetrieveTransferCapability(
				cuid, ts, "SCU");

		tc.setExtInfoBoolean(0, this.relationQR);

		if (this.noExtNegotiation)
			tc.setExtInfo(null);
		return tc;
	}

	private TransferCapability mkFindTC(String cuid, String[] ts) {
		ExtQueryTransferCapability tc = new ExtQueryTransferCapability(cuid,
				ts, "SCU");

		tc.setExtInfoBoolean(0, this.relationQR);

		tc.setExtInfoBoolean(1, this.dateTimeMatching);

		tc.setExtInfoBoolean(2, this.fuzzySemanticPersonNameMatching);

		if (this.noExtNegotiation)
			tc.setExtInfo(null);
		return tc;
	}

	public void setQueryLevel(QueryRetrieveLevel qrlevel) {
		this.qrlevel = qrlevel;
		this.keys.putString(524370, VR.CS, qrlevel.getCode());
	}

	public final void addPrivate(String cuid) {
		this.privateFind.add(cuid);
	}

	public void setCFind(boolean cfind) {
		this.cfind = cfind;
	}

	public boolean isCFind() {
		return this.cfind;
	}

	public void setCGet(boolean cget) {
		this.cget = cget;
	}

	public boolean isCGet() {
		return this.cget;
	}

	public void setMoveDest(String aet) {
		this.moveDest = aet;
	}

	public boolean isCMove() {
		return this.moveDest != null;
	}

	private static int toPort(String port) {
		return port != null ? parseInt(port, "illegal port number", 1, 65535)
				: 104;
	}

	private static int parseInt(String s, String errPrompt, int min, int max) {
		try {
			int i = Integer.parseInt(s);
			if ((i >= min) && (i <= max))
				return i;
		} catch (NumberFormatException e) {
		}
		exit(errPrompt);
		throw new RuntimeException();
	}

	private static String[] split(String s, char delim) {
		String[] s2 = { s, null };
		int pos = s.indexOf(delim);
		if (pos != -1) {
			s2[0] = s.substring(0, pos);
			s2[1] = s.substring(pos + 1);
		}
		return s2;
	}

	private static void exit(String msg) {
		System.err.println(msg);
		System.err.println("Try 'dcmqr -h' for more information.");
	}

	public void start() throws IOException {
		if (this.conn.isListening()) {
			this.conn.bind(this.executor);
			System.out.println("Start Server listening on port "
					+ this.conn.getPort());
		}
	}

	public void stop() {
		if (this.conn.isListening())
			this.conn.unbind();
	}

	public void open() throws IOException, ConfigurationException,
			InterruptedException {
		this.assoc = this.ae.connect(this.remoteAE, this.executor);
	}

	public DicomObject getKeys() {
		return this.keys;
	}

	public List<DicomObject> query() throws IOException, InterruptedException {
		List result = new ArrayList();
		TransferCapability tc = selectFindTransferCapability();
		String cuid = tc.getSopClass();
		String tsuid = selectTransferSyntax(tc);
		if ((tc.getExtInfoBoolean(0)) || (containsUpperLevelUIDs(cuid))) {
			DimseRSP rsp = this.assoc.cfind(cuid, this.priority, this.keys,
					tsuid, this.cancelAfter);
			while (rsp.next()) {
				DicomObject cmd = rsp.getCommand();
				if (CommandUtils.isPending(cmd)) {
					DicomObject data = rsp.getDataset();
					result.add(data);
				}
			}
		} else {
			List upperLevelUIDs = queryUpperLevelUIDs(cuid, tsuid);
			List rspList = new ArrayList(upperLevelUIDs.size());
			int i = 0;
			for (int n = upperLevelUIDs.size(); i < n; i++) {
				((DicomObject) upperLevelUIDs.get(i)).copyTo(this.keys);

				rspList.add(this.assoc.cfind(cuid, this.priority, this.keys,
						tsuid, this.cancelAfter));
			}
			i = 0;
			for (int n = rspList.size(); i < n; i++) {
				DimseRSP rsp = (DimseRSP) rspList.get(i);
				for (int j = 0; rsp.next(); j++) {
					DicomObject cmd = rsp.getCommand();
					if (CommandUtils.isPending(cmd)) {
						DicomObject data = rsp.getDataset();
						result.add(data);
					}
				}

			}

		}

		return result;
	}

	private boolean containsUpperLevelUIDs(String cuid) {
		switch (qrlevel) {
		case IMAGE:
			if (!keys.containsValue(Tag.SeriesInstanceUID)) {
				return false;
			}
			// fall through
		case SERIES:
			if (!keys.containsValue(Tag.StudyInstanceUID)) {
				return false;
			}
			// fall through
		case STUDY:
			if (Arrays.asList(PATIENT_LEVEL_FIND_CUID).contains(cuid)
					&& !keys.containsValue(Tag.PatientID)) {
				return false;
			}
			// fall through
		case PATIENT:
			// fall through
		}
		return true;
	}

	private List<DicomObject> queryUpperLevelUIDs(String cuid, String tsuid)
			throws IOException, InterruptedException {
		List keylist = new ArrayList();
		if (Arrays.asList(PATIENT_LEVEL_FIND_CUID).contains(cuid)) {
			queryPatientIDs(cuid, tsuid, keylist);
			if (this.qrlevel == QueryRetrieveLevel.STUDY) {
				return keylist;
			}
			keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist, 2097165,
					STUDY_MATCHING_KEYS, QueryRetrieveLevel.STUDY);
		} else {
			keylist.add(new BasicDicomObject());
			keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist, 2097165,
					PATIENT_STUDY_MATCHING_KEYS, QueryRetrieveLevel.STUDY);
		}

		if (this.qrlevel == QueryRetrieveLevel.IMAGE) {
			keylist = queryStudyOrSeriesIUIDs(cuid, tsuid, keylist, 2097166,
					SERIES_MATCHING_KEYS, QueryRetrieveLevel.SERIES);
		}

		return keylist;
	}

	private void queryPatientIDs(String cuid, String tsuid,
			List<DicomObject> keylist) throws IOException, InterruptedException {
		String patID = this.keys.getString(1048608);
		String issuer = this.keys.getString(1048609);
		if (patID != null) {
			DicomObject patIdKeys = new BasicDicomObject();
			patIdKeys.putString(1048608, VR.LO, patID);
			if (issuer != null) {
				patIdKeys.putString(1048609, VR.LO, issuer);
			}
			keylist.add(patIdKeys);
		} else {
			DicomObject patLevelQuery = new BasicDicomObject();
			this.keys.subSet(PATIENT_MATCHING_KEYS).copyTo(patLevelQuery);
			patLevelQuery.putNull(1048608, VR.LO);
			patLevelQuery.putNull(1048609, VR.LO);
			patLevelQuery.putString(524370, VR.CS, "PATIENT");

			DimseRSP rsp = this.assoc.cfind(cuid, this.priority, patLevelQuery,
					tsuid, 2147483647);

			for (int i = 0; rsp.next(); i++) {
				DicomObject cmd = rsp.getCommand();
				if (CommandUtils.isPending(cmd)) {
					DicomObject data = rsp.getDataset();

					DicomObject patIdKeys = new BasicDicomObject();
					patIdKeys
							.putString(1048608, VR.LO, data.getString(1048608));

					issuer = this.keys.getString(1048609);
					if (issuer != null) {
						patIdKeys.putString(1048609, VR.LO, issuer);
					}

					keylist.add(patIdKeys);
				}
			}
		}
	}

	private List<DicomObject> queryStudyOrSeriesIUIDs(String cuid,
			String tsuid, List<DicomObject> upperLevelIDs, int uidTag,
			int[] matchingKeys, QueryRetrieveLevel qrLevel) throws IOException,
			InterruptedException {
		List keylist = new ArrayList();
		String uid = this.keys.getString(uidTag);
		for (DicomObject upperLevelID : upperLevelIDs) {
			if (uid != null) {
				DicomObject suidKey = new BasicDicomObject();
				upperLevelID.copyTo(suidKey);
				suidKey.putString(uidTag, VR.UI, uid);
				keylist.add(suidKey);
			} else {
				DicomObject keys2 = new BasicDicomObject();
				this.keys.subSet(matchingKeys).copyTo(keys2);
				upperLevelID.copyTo(keys2);
				keys2.putNull(uidTag, VR.UI);
				keys2.putString(524370, VR.CS, qrLevel.getCode());

				DimseRSP rsp = this.assoc.cfind(cuid, this.priority, keys2,
						tsuid, 2147483647);

				for (int i = 0; rsp.next(); i++) {
					DicomObject cmd = rsp.getCommand();
					if (CommandUtils.isPending(cmd)) {
						DicomObject data = rsp.getDataset();

						DicomObject suidKey = new BasicDicomObject();
						upperLevelID.copyTo(suidKey);
						suidKey.putString(uidTag, VR.UI, data.getString(uidTag));
						keylist.add(suidKey);
					}
				}
			}
		}
		return keylist;
	}

	public TransferCapability selectFindTransferCapability()
			throws NoPresentationContextException {
		TransferCapability tc;
		if ((tc = selectTransferCapability(this.privateFind)) != null)
			return tc;
		if ((tc = selectTransferCapability(this.qrlevel.getFindClassUids())) != null)
			return tc;
		throw new NoPresentationContextException(UIDDictionary.getDictionary()
				.prompt(this.qrlevel.getFindClassUids()[0])
				+ " not supported by " + this.remoteAE.getAETitle());
	}

	public String selectTransferSyntax(TransferCapability tc) {
		String[] tcuids = tc.getTransferSyntax();
		if (Arrays.asList(tcuids).indexOf("1.2.840.10008.1.2.1.99") != -1)
			return "1.2.840.10008.1.2.1.99";
		return tcuids[0];
	}

	public void move(List<DicomObject> findResults) throws IOException,
			InterruptedException {
		if (this.moveDest == null)
			throw new IllegalStateException("moveDest == null");
		TransferCapability tc = selectTransferCapability(this.qrlevel
				.getMoveClassUids());
		if (tc == null) {
			throw new NoPresentationContextException(UIDDictionary
					.getDictionary().prompt(this.qrlevel.getMoveClassUids()[0])
					+ " not supported by " + this.remoteAE.getAETitle());
		}

		String cuid = tc.getSopClass();
		String tsuid = selectTransferSyntax(tc);
		int i = 0;
		for (int n = Math.min(findResults.size(), this.cancelAfter); i < n; i++) {
			DicomObject keys = ((DicomObject) findResults.get(i))
					.subSet(MOVE_KEYS);
			if ((isEvalRetrieveAET())
					&& (containsMoveDest(((DicomObject) findResults.get(i))
							.getStrings(524372)))) {
				continue;
			}

			this.assoc.cmove(cuid, this.priority, keys, tsuid, this.moveDest,
					this.rspHandler);
		}

		this.assoc.waitForDimseRSP();
	}

	private boolean containsMoveDest(String[] retrieveAETs) {
		if (retrieveAETs != null) {
			for (String aet : retrieveAETs) {
				if (this.moveDest.equals(aet)) {
					return true;
				}
			}
		}
		return false;
	}

	public void get(List<DicomObject> findResults) throws IOException,
			InterruptedException {
		TransferCapability tc = selectTransferCapability(this.qrlevel
				.getGetClassUids());
		if (tc == null) {
			throw new NoPresentationContextException(UIDDictionary
					.getDictionary().prompt(this.qrlevel.getGetClassUids()[0])
					+ " not supported by " + this.remoteAE.getAETitle());
		}

		String cuid = tc.getSopClass();
		String tsuid = selectTransferSyntax(tc);
		int i = 0;
		for (int n = Math.min(findResults.size(), this.cancelAfter); i < n; i++) {
			DicomObject keys = ((DicomObject) findResults.get(i))
					.subSet(MOVE_KEYS);

			this.assoc.cget(cuid, this.priority, keys, tsuid, this.rspHandler);
		}
		this.assoc.waitForDimseRSP();
	}

	protected void onMoveRSP(Association as, DicomObject cmd, DicomObject data) {
		if (!CommandUtils.isPending(cmd)) {
			this.completed += cmd.getInt(4129);
			this.warning += cmd.getInt(4131);
			this.failed += cmd.getInt(4130);
		}
	}

	public TransferCapability selectTransferCapability(String[] cuid) {
		for (int i = 0; i < cuid.length; i++) {
			TransferCapability tc = this.assoc
					.getTransferCapabilityAsSCU(cuid[i]);
			if (tc != null)
				return tc;
		}
		return null;
	}

	public TransferCapability selectTransferCapability(List<String> cuid) {
		int i = 0;
		for (int n = cuid.size(); i < n; i++) {
			TransferCapability tc = this.assoc
					.getTransferCapabilityAsSCU((String) cuid.get(i));
			if (tc != null)
				return tc;
		}
		return null;
	}

	public void close() throws InterruptedException {
		this.assoc.release(true);
	}

	public void setStoreDestination(String filePath) {
		this.storeDest = new File(filePath);
		this.devnull = "/dev/null".equals(filePath);
		if (!this.devnull)
			this.storeDest.mkdir();
	}

	public void initTLS() throws GeneralSecurityException, IOException {
		KeyStore keyStore = loadKeyStore(this.keyStoreURL,
				this.keyStorePassword);
		KeyStore trustStore = loadKeyStore(this.trustStoreURL,
				this.trustStorePassword);
		this.device.initTLS(keyStore,
				this.keyPassword != null ? this.keyPassword
						: this.keyStorePassword, trustStore);
	}

	private static KeyStore loadKeyStore(String url, char[] password)
			throws GeneralSecurityException, IOException {
		KeyStore key = KeyStore.getInstance(toKeyStoreType(url));
		InputStream in = openFileOrURL(url);
		try {
			key.load(in, password);
		} finally {
			in.close();
		}
		return key;
	}

	private static InputStream openFileOrURL(String url) throws IOException {
		if (url.startsWith("resource:")) {
			return DcmQR.class.getClassLoader().getResourceAsStream(
					url.substring(9));
		}
		try {
			return new URL(url).openStream();
		} catch (MalformedURLException e) {
		}
		return new FileInputStream(url);
	}

	private static String toKeyStoreType(String fname) {
		return (fname.endsWith(".p12")) || (fname.endsWith(".P12")) ? "PKCS12"
				: "JKS";
	}

	private static enum CUID {
		CR("1.2.840.10008.5.1.4.1.1.1"), CT("1.2.840.10008.5.1.4.1.1.2"), MR(
				"1.2.840.10008.5.1.4.1.1.4"), US("1.2.840.10008.5.1.4.1.1.6.1"), NM(
				"1.2.840.10008.5.1.4.1.1.20"), PET(
				"1.2.840.10008.5.1.4.1.1.128"), SC("1.2.840.10008.5.1.4.1.1.7"), XA(
				"1.2.840.10008.5.1.4.1.1.12.1"), XRF(
				"1.2.840.10008.5.1.4.1.1.12.2"), DX(
				"1.2.840.10008.5.1.4.1.1.1.1"), MG(
				"1.2.840.10008.5.1.4.1.1.1.2"), PR(
				"1.2.840.10008.5.1.4.1.1.11.1"), KO(
				"1.2.840.10008.5.1.4.1.1.88.59"), SR(
				"1.2.840.10008.5.1.4.1.1.88.11");

		final String uid;

		private CUID(String uid) {
			this.uid = uid;
		}
	}

	private static enum TS {
		IVLE(DcmQR.IVRLE_TS), LE(DcmQR.NATIVE_LE_TS), BE(DcmQR.NATIVE_BE_TS), DEFL(
				DcmQR.DEFLATED_TS), JPLL(DcmQR.JPLL_TS), JPLY(DcmQR.JPLY_TS), MPEG2(
				DcmQR.MPEG2_TS), NOPX(DcmQR.NOPX_TS), NOPXD(DcmQR.NOPXDEFL_TS);

		final String[] uids;

		private TS(String[] uids) {
			this.uids = uids;
		}
	}

	public static enum QueryRetrieveLevel {
		PATIENT("PATIENT", DcmQR.PATIENT_RETURN_KEYS,
				DcmQR.PATIENT_LEVEL_FIND_CUID, DcmQR.PATIENT_LEVEL_GET_CUID,
				DcmQR.PATIENT_LEVEL_MOVE_CUID),

		STUDY("STUDY", DcmQR.STUDY_RETURN_KEYS, DcmQR.STUDY_LEVEL_FIND_CUID,
				DcmQR.STUDY_LEVEL_GET_CUID, DcmQR.STUDY_LEVEL_MOVE_CUID),

		SERIES("SERIES", DcmQR.SERIES_RETURN_KEYS,
				DcmQR.SERIES_LEVEL_FIND_CUID, DcmQR.SERIES_LEVEL_GET_CUID,
				DcmQR.SERIES_LEVEL_MOVE_CUID),

		IMAGE("IMAGE", DcmQR.INSTANCE_RETURN_KEYS,
				DcmQR.SERIES_LEVEL_FIND_CUID, DcmQR.SERIES_LEVEL_GET_CUID,
				DcmQR.SERIES_LEVEL_MOVE_CUID);

		private final String code;
		private final int[] returnKeys;
		private final String[] findClassUids;
		private final String[] getClassUids;
		private final String[] moveClassUids;

		private QueryRetrieveLevel(String code, int[] returnKeys,
				String[] findClassUids, String[] getClassUids,
				String[] moveClassUids) {
			this.code = code;
			this.returnKeys = returnKeys;
			this.findClassUids = findClassUids;
			this.getClassUids = getClassUids;
			this.moveClassUids = moveClassUids;
		}

		public String getCode() {
			return this.code;
		}

		public int[] getReturnKeys() {
			return this.returnKeys;
		}

		public String[] getFindClassUids() {
			return this.findClassUids;
		}

		public String[] getGetClassUids() {
			return this.getClassUids;
		}

		public String[] getMoveClassUids() {
			return this.moveClassUids;
		}
	}
}