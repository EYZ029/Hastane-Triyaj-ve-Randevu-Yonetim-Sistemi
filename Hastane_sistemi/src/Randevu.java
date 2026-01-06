import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Randevu implements Comparable<Randevu> {

    public enum Oncelik { ACIL, NORMAL, DUSUK }

    private String hastaAdSoyad;
    private String tcKimlik;
    private LocalDateTime randevuZamani;
    private Oncelik oncelik;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Randevu(String adSoyad, String tcKimlik, String zamanStr, Oncelik oncelik) {
        this.hastaAdSoyad = adSoyad;
        this.tcKimlik = tcKimlik;
        this.randevuZamani = LocalDateTime.parse(zamanStr, FORMATTER);
        this.oncelik = oncelik;
    }

    // Getter Metotları
    public String getHastaAdSoyad() { return hastaAdSoyad; }
    public String getTcKimlik() { return tcKimlik; }
    public Oncelik getOncelik() { return oncelik; }

    // Eşitlik kontrolü (T.C. No'ya göre)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Randevu randevu = (Randevu) o;
        return Objects.equals(tcKimlik, randevu.tcKimlik);
    }
    @Override
    public int hashCode() {
        return Objects.hash(tcKimlik);
    }

    // Triyaj Mantığı: Önce Zaman (Erkenlik), sonra Öncelik (Aciliyet)
    @Override
    public int compareTo(Randevu digerRandevu) {

        // 1. Birincil Kriter: Randevu Zamanına göre karşılaştır.
        // En erken tarih/saat önde gelir.
        int zamanKarsilastir = this.randevuZamani.compareTo(digerRandevu.randevuZamani);

        if (zamanKarsilastir != 0) {
            return zamanKarsilastir; // Zamanlar farklıysa, hemen zamanı uygula.
        }

        // 2. İkincil Kriter: Zamanlar aynıysa, Öncelik seviyelerine göre sırala.
        // ACIL < NORMAL < DUSUK olduğu için, compareTo en acil olanı öne çeker.
        return this.oncelik.compareTo(digerRandevu.oncelik);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %s", oncelik.name(), hastaAdSoyad, tcKimlik, randevuZamani.format(FORMATTER));
    }
}
