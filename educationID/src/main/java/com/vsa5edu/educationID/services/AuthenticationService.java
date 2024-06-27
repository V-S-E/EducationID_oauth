package com.vsa5edu.educationID.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vsa5edu.educationID.database.DTO.TrustedService;
import com.vsa5edu.educationID.database.DTO.User;
import com.vsa5edu.educationID.database.DatabaseContext;
import com.vsa5edu.educationID.mongodb.Session;
import com.vsa5edu.educationID.mongodb.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthenticationService {
    private String privateKey;
    private String publicKey;

    //Get RSA keys from file
    public AuthenticationService() throws IOException {
        //load keys
        InputStream private_is = ResourceLoader.class
                .getResourceAsStream("/RSAKeys/private.txt");
        privateKey = new String(private_is.readAllBytes()).replace("\n", "");
        InputStream public_is = ResourceLoader.class
                .getResourceAsStream("/RSAKeys/public.txt");
        publicKey = new String(public_is.readAllBytes()).replace("\n", "");
    }
    //redis component
/*    @Autowired
    JedisConnect jedisConnect;*/

    @Autowired
    SessionRepository sessionsRepository;
    //sql session
    @Autowired
    DatabaseContext sqlcontext;

    /*    private <T> byte[] objectToBytes(T o) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            byte[] dataBytes = bos.toByteArray();
            oos.close();
            bos.close();
            return dataBytes;
        }

        private <T> T bytesToObject(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            bis.close();
            ois.close();
            return (T)o;
        }*/
    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(spec);
        return pk;
    }

    private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(spec);
        return pk;
    }

    public byte[] generateRandom() {
        byte[] randomResult = new byte[32];
        SecureRandom generator = new SecureRandom();
        generator.nextBytes(randomResult);
        return randomResult;
    }

    private String generateSessionID() {
        byte[] randomResult = new byte[8];
        SecureRandom generator = new SecureRandom();
        generator.nextBytes(randomResult);
        return Base64.getEncoder().encodeToString(randomResult);
    }

    private KeyPair generateRSAKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    private String getHash256(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        String result = Base64.getEncoder().encodeToString(hash);
        return result;
    }

    public String generateSalt() {
        return Base64.getEncoder().encodeToString(generateRandom());
    }

    public String getHash256WithSalt(String value, String salt) throws NoSuchAlgorithmException {
        String valueSalt = salt + value;
        return getHash256(valueSalt);
    }

    private String generateJWT(Long user_id, Integer service_id, String session_id) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String token = JWT.create()
                //for user id
                .withSubject(String.valueOf(user_id))
                //1 hour
                .withExpiresAt(Instant.now().plusSeconds(60*60))
                .withClaim("service_id", service_id)
                .withClaim("session_id", session_id)
                //sign rs256 (private - encode, public - decode)
                .sign(Algorithm.RSA256(((RSAPublicKey) getPublicKey()), (RSAPrivateKey) getPrivateKey()));
        return token;
    }


    //for session
    private String generateRefreshToken() {
        //generate random value
        //encode for rsa
        byte[] randomBytes = generateRandom();
        String token = Base64.getUrlEncoder().encodeToString(randomBytes);
        return token;
    }

    private String signString(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        //byte[] bytesToken = Base64.getDecoder().decode(value);
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        byte[] secretBytes = encryptCipher.doFinal(value.getBytes());
        String base64signStr = Base64.getUrlEncoder().encodeToString(secretBytes);
        return base64signStr;
    }

    private String unsignString(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytesToken = Base64.getUrlDecoder().decode(value);
        Cipher decryptChiper = Cipher.getInstance("RSA");
        decryptChiper.init(Cipher.DECRYPT_MODE, getPrivateKey());
        byte[] decryptBytes = decryptChiper.doFinal(bytesToken);
        return new String(decryptBytes);
    }

    //with password hash and salt, login or phone number (create login class)
    public User checkAuthentication(String passwordHash, String login, String phone) throws Exception {
        //check regex
        User findUser;
        if (!login.isEmpty()) {
            findUser = sqlcontext.streamOf(User.class)
                    .filter(u -> u.login.equals(login))
                    .findFirst().get();
        } else if (phone != null) {
            findUser = sqlcontext.streamOf(User.class)
                    .filter(u -> u.priorityPhone.phoneNumber.equals(phone))
                    .findFirst().get();
        } else throw new Exception("login and phone is null");
        if (passwordHash.equals(findUser.passwordHash)) {
            return findUser;
        } else {
            throw new Exception("Authentication is not valid");
        }
    }
    public TrustedService getTrustedService(String service_domain) throws Exception {
        Optional<TrustedService> service = sqlcontext.streamOf(TrustedService.class)
                .filter(e -> service_domain.startsWith(e.domainName))
                .findFirst();
        if (service.isEmpty())
            throw new Exception("Service is NOT find!");
        return service.get();
    }

    public User getUserById(Long id) throws Exception {
        Optional<User> user = sqlcontext.streamOf(User.class)
                .filter(e -> e.id == id)
                .findFirst();
        if (user.isEmpty()) {
            throw new Exception("User is not found");
        }
        return user.get();
    }

    public String getUserPasswordSalt(String login, String phone) throws Exception {
        //regex login or phone
        String userPasswordSalt;
        if (!login.isEmpty()) {
            userPasswordSalt = sqlcontext.streamOf(User.class)
                    .filter(u -> u.login.equals(login))
                    .findFirst().map(u -> u.passwordSalt).get();
        } else if (phone != null) {
            userPasswordSalt = sqlcontext.streamOf(User.class)
                    .filter(u -> u.priorityPhone.phoneNumber.equals(phone))
                    .findFirst().map(u -> u.passwordSalt).get();
        } else throw new Exception("login and phone is null");
        return userPasswordSalt;
    }

    //for filters
    public DecodedJWT checkJWT(String JWTToken) throws Exception {
        DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(((RSAPublicKey) getPublicKey()), (RSAPrivateKey) getPrivateKey()))
                .build()
                .verify(JWTToken);
        //if decodedJWT is null then no verify
        if (decodedJWT == null)
            throw new Exception("Token is NOT verify!");
        return decodedJWT;
    }

    //new session
    public String getSignAuthorizationToken(User user, TrustedService service, String userAgent) throws Exception {
        //Check if it has session for user
        Session findSession = sessionsRepository
                .findOneByUserIDAndServiceIDAndUserAgent(user.id, service.id, userAgent);
        if (findSession != null) {
            throw new Exception("User and device have session");
        }

        //create session
        String session_id = generateSessionID();
        String refresh = generateRefreshToken();
        String authorizationToken = generateRefreshToken();

        Session session = new Session();
        session.sessionID = session_id;
        session.userID = user.id;
        session.serviceID = service.id;
        session.refreshToken = refresh;
        session.userAgent = userAgent;
        session.authorizationToken = authorizationToken;
        session.startDateTime = Instant.now();

        sessionsRepository.save(session);

        return signString(authorizationToken);
    }

    public Pair<String, String> exchangeTokenOnAccessRefresh(String signedAuthToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String unsignedAuthToken = unsignString(signedAuthToken);
        Session session = sessionsRepository.findOneByAuthorizationToken(unsignedAuthToken);

        String jwt = generateJWT(session.userID, session.serviceID, session.sessionID);
        String refresh = session.refreshToken;
        //drop authToken
        session.authorizationToken = "";
        sessionsRepository.save(session);
        //return jwt_refresh
        return Pair.of(jwt, signString(refresh));
    }

    //check session - refresh token
    public Pair<String, String> refreshJWT(String refreshToken) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String unsignRefresh = unsignString(refreshToken);
        Session currentSession = sessionsRepository.findOneByRefreshToken(unsignRefresh);
        String newToken = generateJWT(currentSession.userID
                , currentSession.serviceID
                , currentSession.sessionID);
        String newRefresh = generateRefreshToken();
        //update refresh
        currentSession.refreshToken = newRefresh;
        sessionsRepository.save(currentSession);

        //return new data
        Pair<String, String> result = Pair.of(newToken, signString(newRefresh));
        return result;
    }

    //delete session
    public void deleteSession(String JWTToken) throws Exception {
        DecodedJWT jwt = checkJWT(JWTToken);
        String session_id = jwt.getClaim("session_id").asString();
        sessionsRepository.deleteBySessionID(session_id);
    }


}
