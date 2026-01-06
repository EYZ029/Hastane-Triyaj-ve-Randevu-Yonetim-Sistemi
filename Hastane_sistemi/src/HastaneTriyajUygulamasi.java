import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class HastaneTriyajUygulamasi {

    // --- KONTROL: VERİ ALANLARI ---
    private PriorityQueue<Randevu> randevuKuyrugu = new PriorityQueue<>();

    // --- GUI ALANLARI ---
    private JFrame cerceve;
    private JTextField adField, tcField, tarihSaatField;
    private JComboBox<Randevu.Oncelik> oncelikCombo;
    private JTextArea kuyrukGoruntuleme;

    public HastaneTriyajUygulamasi() {
        cerceve = new JFrame("Hastane Triyaj ve Randevu Yönetimi (Zaman Öncelikli)");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setLayout(new BorderLayout(10, 10));

        setupInputPanel();
        setupButtons();
        setupDisplayPanel();

        cerceve.pack();
        cerceve.setLocationRelativeTo(null);
        cerceve.setVisible(true);

        kuyruguGuncelle();
    }

    // --- GUI KURULUM METOTLARI ---

    private void setupInputPanel() {
        JPanel girisPaneli = new JPanel(new GridBagLayout());
        girisPaneli.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "📋 Yeni Randevu Girişi ve Bilgileri",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // 1. Ad Soyad
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        girisPaneli.add(new JLabel("Ad Soyad:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        adField = new JTextField(18);
        girisPaneli.add(adField, gbc);

        // 2. T.C. No
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        girisPaneli.add(new JLabel("T.C. Kimlik No:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        tcField = new JTextField(18);
        girisPaneli.add(tcField, gbc);

        // 3. Tarih/Saat
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        girisPaneli.add(new JLabel("Tarih (YYYY-MM-DD HH:mm):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        tarihSaatField = new JTextField("2026-01-05 10:30", 18);
        girisPaneli.add(tarihSaatField, gbc);

        // 4. Öncelik
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        girisPaneli.add(new JLabel("🚨 Öncelik Seviyesi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        oncelikCombo = new JComboBox<>(Randevu.Oncelik.values());
        oncelikCombo.setPreferredSize(new Dimension(180, 25));
        girisPaneli.add(oncelikCombo, gbc);

        cerceve.add(girisPaneli, BorderLayout.NORTH);
    }

    private void setupButtons() {
        JPanel butonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));

        JButton ekleButonu = new JButton("➕ Randevu Ekle");
        JButton siradakiIsleButonu = new JButton("▶️ Sıradaki En Öncelikliyi İşle");
        JButton randevuSilButonu = new JButton("❌ Randevuyu T.C. No ile İptal Et");

        ekleButonu.setBackground(new Color(200, 255, 200));
        siradakiIsleButonu.setBackground(new Color(255, 255, 200));
        randevuSilButonu.setBackground(new Color(255, 200, 200));

        ekleButonu.addActionListener(new EklemeDinleyici());
        siradakiIsleButonu.addActionListener(new IslemeDinleyici());
        randevuSilButonu.addActionListener(new SilmeDinleyici());

        butonPaneli.add(ekleButonu);
        butonPaneli.add(siradakiIsleButonu);
        butonPaneli.add(randevuSilButonu);

        cerceve.add(butonPaneli, BorderLayout.SOUTH);
    }

    private void setupDisplayPanel() {
        kuyrukGoruntuleme = new JTextArea(15, 60);
        kuyrukGoruntuleme.setEditable(false);
        kuyrukGoruntuleme.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(kuyrukGoruntuleme);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE),
                "⏳ Öncelikli Kuyruk Listesi (En Erken Randevu Saati Üstte)",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP
        ));

        cerceve.add(scrollPane, BorderLayout.CENTER);
    }

    // --- KONTROL: İŞ MANTIK METOTLARI ---

    private void kuyruguGuncelle() {
        // stream().sorted() metodu, Randevu.compareTo() metodunu kullanarak doğru sıralamayı yapar.
        String gorunum = randevuKuyrugu.stream()
                .sorted()
                .map(Randevu::toString)
                .collect(Collectors.joining("\n"));

        kuyrukGoruntuleme.setText(gorunum.isEmpty() ? "\n\n   Kuyrukta bekleyen randevu bulunmamaktadır." : gorunum);
    }

    private class EklemeDinleyici implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tcField.getText().trim().isEmpty() || adField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(cerceve, "Ad ve T.C. Kimlik No boş bırakılamaz.", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Randevu yeniRandevu = new Randevu(
                        adField.getText().trim(),
                        tcField.getText().trim(),
                        tarihSaatField.getText().trim(),
                        (Randevu.Oncelik) oncelikCombo.getSelectedItem()
                );

                randevuKuyrugu.offer(yeniRandevu);
                kuyruguGuncelle();

                JOptionPane.showMessageDialog(cerceve, "Randevu başarıyla eklendi ve zaman önceliğine göre sıralandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                adField.setText("");
                tcField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(cerceve, "Hata: Tarih/Saat formatını (YYYY-MM-DD HH:mm) veya diğer girişleri kontrol edin.", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class IslemeDinleyici implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (randevuKuyrugu.isEmpty()) {
                JOptionPane.showMessageDialog(cerceve, "Kuyrukta bekleyen randevu bulunmamaktadır.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Randevu siradaki = randevuKuyrugu.poll();

            JOptionPane.showMessageDialog(cerceve,
                    "İŞLENDİ: " + siradaki.getHastaAdSoyad() + " (" + siradaki.getOncelik().name() + ")",
                    "Sıradaki Hasta Çağrıldı",
                    JOptionPane.WARNING_MESSAGE
            );

            kuyruguGuncelle();
        }
    }

    private class SilmeDinleyici implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String silinecekTc = JOptionPane.showInputDialog(cerceve, "İptal etmek istediğiniz randevunun T.C. Kimlik Numarasını girin:");

            if (silinecekTc != null && !silinecekTc.trim().isEmpty()) {
                Randevu silinecekDummy = new Randevu("", silinecekTc.trim(), "2000-01-01 00:00", Randevu.Oncelik.DUSUK);

                boolean silindi = randevuKuyrugu.remove(silinecekDummy);

                if (silindi) {
                    JOptionPane.showMessageDialog(cerceve, silinecekTc + " T.C. Nolu randevu başarıyla İPTAL EDİLDİ.", "İptal Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    kuyruguGuncelle();
                } else {
                    JOptionPane.showMessageDialog(cerceve, "Bu T.C. Numarasına ait randevu kuyrukta bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HastaneTriyajUygulamasi::new);
    }
}
