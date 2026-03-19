import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class RfidPayment {

    // ⚠️ Remplace par l'IP de ton PC Windows sur le réseau local
    private static final String API_URL = "http://10.3.203.4:8080";
    private static final String SCRIPT_PATH = "/home/pi/script/tag_detect.sh";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== Mode Paiement RFID ===");
        System.out.print("ID du commerçant : ");
        String merchantId = scanner.nextLine();

        System.out.print("Montant à débiter (ex: 5.00) : ");
        String montant = scanner.nextLine();

        System.out.println("Approchez le badge...");

        // Lire l'UID via le script shell
        String uid = lireUidBadge();

        if (uid == null || uid.isBlank()) {
            System.out.println("Aucun badge détecté.");
            return;
        }

        // L'UID est en hex (ex: "a1b2c3d4"), on le nettoie
        uid = uid.trim().toLowerCase();
        System.out.println("Badge détecté : " + uid);

        // Envoyer le paiement à l'API
        String json = String.format(
            "{\"uidRfid\":\"%s\",\"montant\":%s,\"merchantId\":%s}",
            uid, montant, merchantId
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + "/api/payment"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            System.out.println("✓ Paiement accepté !");
        } else {
            System.out.println("✗ Refusé : " + response.body());
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
}