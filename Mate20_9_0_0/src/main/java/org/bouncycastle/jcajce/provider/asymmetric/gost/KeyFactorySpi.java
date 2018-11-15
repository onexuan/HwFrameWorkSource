package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        return keySpec instanceof GOST3410PrivateKeySpec ? new BCGOST3410PrivateKey((GOST3410PrivateKeySpec) keySpec) : super.engineGeneratePrivate(keySpec);
    }

    protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        return keySpec instanceof GOST3410PublicKeySpec ? new BCGOST3410PublicKey((GOST3410PublicKeySpec) keySpec) : super.engineGeneratePublic(keySpec);
    }

    protected KeySpec engineGetKeySpec(Key key, Class cls) throws InvalidKeySpecException {
        GOST3410PublicKeyParameterSetSpec publicKeyParameters;
        if (cls.isAssignableFrom(GOST3410PublicKeySpec.class) && (key instanceof GOST3410PublicKey)) {
            GOST3410PublicKey gOST3410PublicKey = (GOST3410PublicKey) key;
            publicKeyParameters = gOST3410PublicKey.getParameters().getPublicKeyParameters();
            return new GOST3410PublicKeySpec(gOST3410PublicKey.getY(), publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA());
        } else if (!cls.isAssignableFrom(GOST3410PrivateKeySpec.class) || !(key instanceof GOST3410PrivateKey)) {
            return super.engineGetKeySpec(key, cls);
        } else {
            GOST3410PrivateKey gOST3410PrivateKey = (GOST3410PrivateKey) key;
            publicKeyParameters = gOST3410PrivateKey.getParameters().getPublicKeyParameters();
            return new GOST3410PrivateKeySpec(gOST3410PrivateKey.getX(), publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA());
        }
    }

    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof GOST3410PublicKey) {
            return new BCGOST3410PublicKey((GOST3410PublicKey) key);
        }
        if (key instanceof GOST3410PrivateKey) {
            return new BCGOST3410PrivateKey((GOST3410PrivateKey) key);
        }
        throw new InvalidKeyException("key type unknown");
    }

    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
            return new BCGOST3410PrivateKey(privateKeyInfo);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("algorithm identifier ");
        stringBuilder.append(algorithm);
        stringBuilder.append(" in key not recognised");
        throw new IOException(stringBuilder.toString());
    }

    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (algorithm.equals(CryptoProObjectIdentifiers.gostR3410_94)) {
            return new BCGOST3410PublicKey(subjectPublicKeyInfo);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("algorithm identifier ");
        stringBuilder.append(algorithm);
        stringBuilder.append(" in key not recognised");
        throw new IOException(stringBuilder.toString());
    }
}