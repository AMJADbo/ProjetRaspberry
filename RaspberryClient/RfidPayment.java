import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class RfidPayment {

    private static final String API_URL = "http://10.3.203.4:8080";
    private static final String SCRIPT_PATH = "/home/pi/script/tag_detect.sh";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Systeme de paiement RFID ===");
        System.out.println("Approchez votre badge pour vous connecter...");

        String uid = lireUidBadge();
        if (uid == null || uid.isBlank()) {
            System.out.println("Aucun badge detecte.");
            return;
        }
        uid = uid.trim().toLowerCase();
        System.out.println("Badge detecte : " + uid);

        String json = "{\"uidRfid\":\"" + uid + "\"}";
        HttpRequest authRequest = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + "/api/auth/badge"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> authResponse = client.send(
            authRequest, HttpResponse.BodyHandlers.ofString()
        );

        if (authResponse.statusCode() != 200) {
            System.out.println("Badge non reconnu ou compte desactive.");
            return;
        }

        String body = authResponse.body();
        String role = extraireValeur(body, "role");
        String nom = extraireValeur(body, "nom");
        System.out.println("Connecte : " + nom + " (" + role + ")");

        switch (role) {
            case "USER":
                modeUtilisateur(client, uid, scanner);
                break;
            case "MERCHANT":
                modeCommercant(client, uid, scanner);
                break;
            case "ADMIN":
                System.out.println("Connecte en tant qu'admin.");
                break;
            default:
                System.out.println("Role inconnu.");
                break;
        }
    }

    private static void modeUtilisateur(HttpClient client, String uid, Scanner scanner) throws Exception {
        System.out.println("\n--- Mode Utilisateur ---");
        System.out.println("1. Voir mon solde");
        System.out.println("2. Recharger mon compte");
        System.out.print("Choix : ");
        String choix = scanner.nextLine();

        if (choix.equals("2")) {
            System.out.print("Montant a recharger : ");
            String montant = scanner.nextLine();

            String json = "{\"uidRfid\":\"" + uid + "\",\"montant\":" + montant + "}";
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/api/account/credit-by-badge"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                System.out.println("Compte recharge avec succes !");
            } else {
                System.out.println("Erreur : " + res.body());
            }
        } else {
            System.out.println("Consultez votre solde sur l'interface web.");
        }
    }

    private static void modeCommercant(HttpClient client, String uid, Scanner scanner) throws Exception {
        System.out.println("\n--- Mode Commercant ---");
        System.out.print("Montant a encaisser : ");
        String montant = scanner.nextLine();

        System.out.println("Demandez au client de passer son badge...");
        String uidClient = lireUidBadge();
        if (uidClient == null || uidClient.isBlank()) {
            System.out.println("Aucun badge client detecte.");
            return;
        }
        uidClient = uidClient.trim().toLowerCase();
        System.out.println("Badge client detecte : " + uidClient);

        String json = "{\"uidRfid\":\"" + uidClient + "\",\"montant\":" + montant + ",\"uidMerchant\":\"" + uid + "\"}";
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + "/api/payment-by-badge"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            System.out.println("Paiement accepte !");
        } else {
            System.out.println("Paiement refuse : " + res.body());
        }
    }

    private static String lireUidBadge() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", SCRIPT_PATH);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        String uid = reader.readLine();
        process.waitFor();
        return uid;
    }

    private static String extraireValeur(String json, String cle) {
        String search = "\"" + cle + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}