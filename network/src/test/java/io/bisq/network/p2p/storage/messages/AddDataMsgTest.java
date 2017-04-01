package io.bisq.network.p2p.storage.messages;

import io.bisq.common.crypto.CryptoException;
import io.bisq.common.crypto.KeyRing;
import io.bisq.common.crypto.KeyStorage;
import io.bisq.common.crypto.SealedAndSigned;
import io.bisq.generated.protobuffer.PB;
import io.bisq.network.p2p.NodeAddress;
import io.bisq.network.p2p.PrefixedSealedAndSignedMsg;
import io.bisq.network.p2p.storage.payload.MailboxStoragePayload;
import io.bisq.network.p2p.storage.payload.ProtectedMailboxStorageEntry;
import io.bisq.network.p2p.storage.payload.ProtectedStorageEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.UUID;

@Slf4j
public class AddDataMsgTest {
    private KeyRing keyRing1;
    private File dir1;


    @Before
    public void setup() throws InterruptedException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, CryptoException, SignatureException, InvalidKeyException {
        Security.addProvider(new BouncyCastleProvider());
        dir1 = File.createTempFile("temp_tests1", "");
        dir1.delete();
        dir1.mkdir();
        keyRing1 = new KeyRing(new KeyStorage(dir1));
    }

    @Test
    public void toProtoBuf() throws Exception {
        SealedAndSigned sealedAndSigned = new SealedAndSigned(RandomUtils.nextBytes(10), RandomUtils.nextBytes(10), RandomUtils.nextBytes(10), keyRing1.getPubKeyRing().getSignaturePubKey());
        PrefixedSealedAndSignedMsg prefixedSealedAndSignedMessage = new PrefixedSealedAndSignedMsg(new NodeAddress("host", 1000), sealedAndSigned, RandomUtils.nextBytes(10),
                UUID.randomUUID().toString());
        MailboxStoragePayload mailboxStoragePayload = new MailboxStoragePayload(prefixedSealedAndSignedMessage,
                keyRing1.getPubKeyRing().getSignaturePubKey(), keyRing1.getPubKeyRing().getSignaturePubKey());
        ProtectedStorageEntry protectedStorageEntry = new ProtectedMailboxStorageEntry(mailboxStoragePayload,
                keyRing1.getSignatureKeyPair().getPublic(), 1, RandomUtils.nextBytes(10), keyRing1.getPubKeyRing().getSignaturePubKey());
        AddDataMsg dataMessage1 = new AddDataMsg(protectedStorageEntry);
        PB.Envelope envelope = dataMessage1.toProto();

        //TODO CoreProtobufferResolver is not accessible here
        // We should refactor it so that the classes themselves know how to deserialize 
        // so we don't get dependencies from core objects here
      /*  AddDataMessage dataMessage2 = (AddDataMessage) ProtoBufferUtilities.getAddDataMessage(envelope);

        assertTrue(dataMessage1.protectedStorageEntry.getStoragePayload().equals(dataMessage2.protectedStorageEntry.getStoragePayload()));
        assertTrue(dataMessage1.protectedStorageEntry.equals(dataMessage2.protectedStorageEntry));
        assertTrue(dataMessage1.equals(dataMessage2));*/
    }

}