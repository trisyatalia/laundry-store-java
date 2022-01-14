/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


/**
 *
 * @author HP
 */
public class Kasir extends javax.swing.JFrame {

    private Statement st;
    private final Connection Con;
    private ResultSet RsProduk;
    private ResultSet Rs;
    private ResultSet RsPegawai;   
    public String ID_Trans, ID_Peg, ID_Detail, kontak_pem, tanggal_mas, tanggal_kel, ID_prod, banyak, kodepetugas, namapetugas, kodeanggota, namaanggota;
    public int harga_satuan, Harga_total, bayar_akhir;
    private String Sql = "";
  
    /**
     * Creates new form Kasir
     */
    public Kasir() {
        initComponents();
        Con = ConnectionClass.getConnection();

        ////////////////////////////
        tampiltransaksi();        
        PilihPegawai();
        PilihProduk();
        autoidtrans();
        Waktu();
        hapusrowkosong();            
    }
    
   

    public void hapusrowkosong() {
        DefaultTableModel model = (DefaultTableModel) tdetail.getModel();
        Object colidtrans = model.getValueAt(1, 2);
        
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            if (colidtrans == null) {
                model.removeRow(i);
            }
        }
    }

    //trisya hahahahha
    public void autoidtrans() {
        try {

            String sql = "select right(id_transaksi,2) as no_akhir from transaksi ORDER BY id_transaksi asc";
            st = Con.createStatement();
            Rs = st.executeQuery(sql);

            if (Rs.first() == false) {
                idtrans.setText("T0001");
            } else {
                Rs.last();
                int no = Rs.getInt(1) + 1;
                String cno = String.valueOf(no);
                int pjg_cno = cno.length();
                for (int i = 0; i < 2 - pjg_cno; i++) {
                    cno = "00" + cno;
                }
                idtrans.setText("T0" + cno);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    //ami  
    private void PilihPegawai() {
        combopeg.removeAllItems();
        combopeg.addItem("Select");

        try {
            Sql = "SELECT id_pegawai FROM pegawai";
            st = Con.createStatement();
            RsPegawai = st.executeQuery(Sql);
            while (RsPegawai.next()) {
                String AliasKode = RsPegawai.getString("ID_PEGAWAI");
                combopeg.addItem(AliasKode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal Menampilkan Id Pegawai\n" + e.getMessage());
        }
    }

    private void PilihProduk() {
        comboproduk.removeAllItems();
        comboproduk.addItem("Select");
        try {
            String Sql = "SELECT*FROM produk";
            Statement st = Con.createStatement();
            RsProduk = st.executeQuery(Sql);
            while (RsProduk.next()) {
                String AliasKode = RsProduk.getString("ID_PRODUK");
                comboproduk.addItem(AliasKode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal Menampilkan Id Produk\n" + e.getMessage());
        }
    }

    private void prosestambah() {

        try {
            DefaultTableModel tableModel = (DefaultTableModel) tdetail.getModel();
            String[] data = new String[5];
            data[0] = idtrans.getText();
            data[1] = String.valueOf(comboproduk.getSelectedItem());
            data[2] = hargasat.getText();
            data[3] = banyk.getText();
            data[4] = hargatot.getText();
            tableModel.addRow(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error masukkan data \n" + e.getMessage());
        }
    }

    private void Total() {
        int jumlahBaris = tdetail.getRowCount();
        int ttlbayar = 0, jlhcuci = 0;
        int Jlhtotal;

        TableModel tblmodel;
        tblmodel = tdetail.getModel();
        for (int i = 0; i < jumlahBaris; i++) {
            Jlhtotal = Integer.parseInt(tblmodel.getValueAt(i, 4).toString());
            jlhcuci = Jlhtotal + jlhcuci;
        }
        bayarakhir.setText(String.valueOf(jlhcuci));
    }

    public void Waktu() {
        Date tgl = new Date();        
        tmasuk.setDate(tgl);
        tkeluar.setDate(tgl);
       
    }

    //dita
    private void simpandetail() {
        
        int jumlah_baris = tdetail.getRowCount();
        if (jumlah_baris == 0) {
            JOptionPane.showMessageDialog(rootPane, "Table is empty!!");
        } else {
            try {
                int i = 0;                
                while (i < jumlah_baris) {                            
                    st.executeUpdate("insert into detail_transaksi"
                            + "(id_transaksi, id_produk, quantity, harga_total)"
                            + "values('" + idtrans.getText() + "',"                            
                            + "'" + comboproduk.getSelectedItem() + "',"                            
                            + "'" + banyk.getText() + "',"
                            + "'" + hargatot.getText() + "');");
                    i++;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "Gagal Menyimpan..!!" + e);
            }
        }
    }

    //ami trisya
    private void tampiltransaksi() {
        DefaultTableModel grid = new DefaultTableModel();
        grid.addColumn("ID Transaksi");
        grid.addColumn("kontak_pembeli");
        grid.addColumn("tanggal_masuk");
        grid.addColumn("tanggal_keluar");
        grid.addColumn("id_pegawai");
        grid.addColumn("bayar_akhir");

        try {
            String sql = "SELECT * FROM transaksi;";
            st = Con.prepareStatement(sql);
            Rs = st.executeQuery(sql);
            DefaultTableModel dtm = (DefaultTableModel) ttrans.getModel();
            dtm.setRowCount(0);
            String[] data = new String[6];
            int i = 1;

            while (Rs.next()) {
                data[0] = Rs.getString("id_transaksi");
                data[1] = Rs.getString("id_pegawai");
                data[2] = Rs.getString("kontak_pembeli");
                data[3] = Rs.getString("tanggal_masuk");
                data[4] = Rs.getString("tanggal_keluar");                
                data[5] = Rs.getString("bayar_akhir");
                dtm.addRow(data);
                i++;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void kosongkan() {
        idtrans.setText("");
        combopeg.setSelectedItem(true);
        kontak.setText("");
        tmasuk.setDate(new Date());        
        tkeluar.setDate(new Date());
        comboproduk.setSelectedItem("Select");
        hargasat.setText("");
        banyk.setText("");
        hargatot.setText("");
        bayarakhir.setText("");
        jTextField1.setText("");

    }


    private void hapustable() {
        DefaultTableModel model = (DefaultTableModel) tdetail.getModel();

        while (model.getRowCount() > 0) {
            for (int i = 0; i < model.getRowCount(); ++i) {
                model.removeRow(i);
            }
        }
    }

    public void hitungTotalSatuan() {
        int harga = 0;
        int total = 0;
        int banyak = 0;

        harga = Integer.parseInt(hargasat.getText());
        banyak = Integer.parseInt(banyk.getText());
        total = harga * banyak;

        hargatot.setText(String.valueOf(total));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        b = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ttrans = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        save = new javax.swing.JLabel();
        keluar = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        kontak = new javax.swing.JTextField();
        bayarakhir = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel19 = new javax.swing.JLabel();
        hargatot = new javax.swing.JTextField();
        jSeparator14 = new javax.swing.JSeparator();
        hargasat = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        idtrans = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        banyk = new javax.swing.JTextField();
        jSeparator18 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tambah = new javax.swing.JLabel();
        kurang = new javax.swing.JLabel();
        tmasuk = new com.toedter.calendar.JDateChooser();
        tkeluar = new com.toedter.calendar.JDateChooser();
        comboproduk = new javax.swing.JComboBox();
        combopeg = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        tdetail = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 610));

        b.setBackground(new java.awt.Color(255, 204, 204));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 48)); // NOI18N
        jLabel1.setText("I WISH YOU WASH HERE");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(291, 291, 291)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        ttrans.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID.TRANSAKSI", "ID.PEGAWAI", "KONTAK PEMBELI", "TANGGAL MASUK", "TANGGAL KELUAR", "BAYAR AKHIR"
            }
        ));
        jScrollPane1.setViewportView(ttrans);

        save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/checkmark.png"))); // NOI18N
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveMouseClicked(evt);
            }
        });

        keluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logout.png"))); // NOI18N
        keluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keluarMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel6.setText("TANGGAL KELUAR");

        jLabel5.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel5.setText("TANGGAL MASUK");

        kontak.setBackground(new java.awt.Color(255, 204, 204));
        kontak.setToolTipText("");
        kontak.setBorder(null);
        kontak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kontakActionPerformed(evt);
            }
        });

        bayarakhir.setEditable(false);
        bayarakhir.setBackground(new java.awt.Color(255, 204, 204));
        bayarakhir.setToolTipText("");
        bayarakhir.setBorder(null);
        bayarakhir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bayarakhirActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel19.setText("HARGA SATUAN");

        hargatot.setEditable(false);
        hargatot.setBackground(new java.awt.Color(255, 204, 204));
        hargatot.setToolTipText("");
        hargatot.setBorder(null);
        hargatot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hargatotActionPerformed(evt);
            }
        });

        hargasat.setEditable(false);
        hargasat.setBackground(new java.awt.Color(255, 204, 204));
        hargasat.setToolTipText("");
        hargasat.setBorder(null);
        hargasat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hargasatActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel20.setText("BANYAK");

        jLabel21.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel21.setText("HARGA TOTAL");

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel2.setText("ID.TRANSAKSI");

        jLabel3.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel3.setText("ID.PEGAWAI");

        jLabel4.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel4.setText("KONTAK PEMBELI");

        idtrans.setEditable(false);
        idtrans.setBackground(new java.awt.Color(255, 204, 204));
        idtrans.setToolTipText("");
        idtrans.setBorder(null);
        idtrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idtransActionPerformed(evt);
            }
        });

        banyk.setBackground(new java.awt.Color(255, 204, 204));
        banyk.setToolTipText("");
        banyk.setBorder(null);
        banyk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banykActionPerformed(evt);
            }
        });
        banyk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                banykKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                banykKeyReleased(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel22.setText("ID.PRODUK");

        jLabel7.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jLabel7.setText("BAYAR AKHIR");

        tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus.png"))); // NOI18N
        tambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tambahMouseClicked(evt);
            }
        });

        kurang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/trash.png"))); // NOI18N
        kurang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                kurangMouseClicked(evt);
            }
        });

        tmasuk.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tmasukPropertyChange(evt);
            }
        });

        tkeluar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tkeluarPropertyChange(evt);
            }
        });

        comboproduk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboproduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboprodukActionPerformed(evt);
            }
        });

        combopeg.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combopeg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combopegActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel13.setText("NAMA PRODUK");

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(255, 204, 204));
        jTextField1.setBorder(null);

        tdetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID TRANSAKSI", "ID PRODUK", "HARGA SATUAN", "QUANTITY", "HARGA TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tdetail);

        javax.swing.GroupLayout bLayout = new javax.swing.GroupLayout(b);
        b.setLayout(bLayout);
        bLayout.setHorizontalGroup(
            bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(bLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bLayout.createSequentialGroup()
                                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel19))
                                .addGap(61, 61, 61)
                                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(hargatot)
                                    .addComponent(jSeparator14)
                                    .addComponent(banyk)
                                    .addComponent(jSeparator18)
                                    .addComponent(hargasat)
                                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(bLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(bLayout.createSequentialGroup()
                                                .addGap(45, 45, 45)
                                                .addComponent(kontak, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(bLayout.createSequentialGroup()
                                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bLayout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(61, 61, 61))
                                            .addGroup(bLayout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(77, 77, 77)))
                                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(idtrans, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                            .addComponent(jSeparator7, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                            .addComponent(combopeg, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGroup(bLayout.createSequentialGroup()
                                    .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bLayout.createSequentialGroup()
                                            .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel5))
                                            .addGap(45, 45, 45))
                                        .addGroup(bLayout.createSequentialGroup()
                                            .addComponent(jLabel13)
                                            .addGap(69, 69, 69)))
                                    .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(tmasuk, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                        .addComponent(tkeluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(comboproduk, 0, 140, Short.MAX_VALUE)
                                        .addComponent(jTextField1)
                                        .addComponent(jSeparator3))))
                            .addComponent(jLabel22))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bLayout.createSequentialGroup()
                        .addComponent(tambah)
                        .addGap(18, 18, 18)
                        .addComponent(kurang)
                        .addGap(58, 58, 58)))
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(bLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(13, 13, 13)
                                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(bLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bLayout.createSequentialGroup()
                                        .addGap(0, 1, Short.MAX_VALUE)
                                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(bayarakhir, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(43, 43, 43))))
                            .addGroup(bLayout.createSequentialGroup()
                                .addGap(140, 140, 140)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addGap(95, 95, 95)
                        .addComponent(jLabel12)
                        .addGap(456, 456, 456))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
            .addGroup(bLayout.createSequentialGroup()
                .addGap(332, 332, 332)
                .addComponent(save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(keluar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        bLayout.setVerticalGroup(
            bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(bLayout.createSequentialGroup()
                                .addComponent(idtrans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(combopeg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(bLayout.createSequentialGroup()
                                .addComponent(kontak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4))
                            .addComponent(tmasuk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(tkeluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(comboproduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(bLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel20)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel21))
                            .addGroup(bLayout.createSequentialGroup()
                                .addComponent(hargasat, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addGroup(bLayout.createSequentialGroup()
                                        .addComponent(banyk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(jSeparator18, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(hargatot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(jSeparator14, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tambah)
                            .addComponent(kurang)))
                    .addGroup(bLayout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel10))))
                    .addGroup(bLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(bayarakhir, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(save)
                    .addComponent(keluar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(bLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel12))
                    .addGroup(bLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(b, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(b, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1219, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void kontakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kontakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kontakActionPerformed

    private void bayarakhirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bayarakhirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bayarakhirActionPerformed

    private void hargatotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hargatotActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hargatotActionPerformed

    private void hargasatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hargasatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hargasatActionPerformed
   
    private void banykActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banykActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_banykActionPerformed

    private void idtransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idtransActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idtransActionPerformed

    private void keluarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_keluarMouseClicked
        // TODO add your handling code here:
        this.setVisible(false);
        new Satu().setVisible(true);
    }//GEN-LAST:event_keluarMouseClicked

    private void comboprodukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboprodukActionPerformed
        // TODO add your handling code here:
        try {
            Sql = "select * from produk where ID_PRODUK='" + comboproduk.getSelectedItem().toString() + "'";
            st = Con.createStatement();
            RsProduk = st.executeQuery(Sql);
            while (RsProduk.next()) {
                jTextField1.setText(RsProduk.getString("nama_produk"));
                hargasat.setText(RsProduk.getString("harga_produk"));
                banyk.setText("");
                hargatot.setText("");

            }
        } catch (Exception e) {
            
      }
    }//GEN-LAST:event_comboprodukActionPerformed

    private void combopegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combopegActionPerformed
        // TODO add your handling code here:
        try {
            Sql = "select * from pegawai where ID_PEGAWAI='" + combopeg.getSelectedItem().toString() + "'";
            st = Con.createStatement();
            RsPegawai = st.executeQuery(Sql);
            while (RsPegawai.next()) {

             
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_combopegActionPerformed

    private void tambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tambahMouseClicked
        // TODO add your handling code here:
        prosestambah();
        Total();        
    }//GEN-LAST:event_tambahMouseClicked

    private void kurangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_kurangMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tdetail.getModel();
        int row = tdetail.getSelectedRow();
        if (row >= 0) {
            int ok = JOptionPane.showConfirmDialog(null, "You sure you want to Delete", "Message", JOptionPane.YES_NO_OPTION);

            if (ok == 0) {
                model.removeRow(row);
            }
        }
        Total();
    }//GEN-LAST:event_kurangMouseClicked

    private void saveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveMouseClicked
        // TODO add your handling code here:
        
        SimpleDateFormat sdfm = new SimpleDateFormat("dd-MMM-yyyy");
        String datemsk = sdfm.format(tmasuk.getDate());
        
        SimpleDateFormat sdfk = new SimpleDateFormat("dd-MMM-yyyy");
        String datekel = sdfk.format(tkeluar.getDate());
        
        ID_Trans = idtrans.getText();
        ID_Peg = combopeg.getItemAt(combopeg.getSelectedIndex()).toString();
        kontak_pem = kontak.getText();
        tanggal_mas = datemsk;
        tanggal_kel = datekel;
        ID_prod = comboproduk.getItemAt(comboproduk.getSelectedIndex()).toString();
        harga_satuan = Integer.parseInt(hargasat.getText());
        banyak = banyk.getText();
        Harga_total = Integer.parseInt(hargatot.getText());
        bayar_akhir = Integer.parseInt(bayarakhir.getText());        
        try {
            Sql = "insert into transaksi"
                    + "(id_transaksi,id_pegawai,kontak_pembeli,tanggal_masuk,tanggal_keluar,bayar_akhir)"
                    + "values('" + ID_Trans + "',"
                    + "'" + ID_Peg + "',"
                    + "'" + kontak_pem + "',"
                    + "'" + tanggal_mas + "',"
                    + "'" + tanggal_kel + "',"
                    + "'" + bayar_akhir + "')";
            st = Con.createStatement();
            st.execute(Sql);     
            simpandetail();
            JOptionPane.showMessageDialog(null, "Data successfully saved");
            
            tampiltransaksi();
            kosongkan();            
            hapustable();
            save.show();
            save.show();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data is not successfully saved, Data that you entered is incorrect" + e.getMessage());
        }
        
        autoidtrans();
    }
    
    private void JTANGGALPropertyChange(java.beans.PropertyChangeEvent evt) {
        // TODO add your handling code here:

    }//GEN-LAST:event_saveMouseClicked


    private void banykKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_banykKeyPressed
        // TODO add your handling code here:
      
    }//GEN-LAST:event_banykKeyPressed

    private void banykKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_banykKeyReleased
        // TODO add your handling code here:
        if (banyk == null) {
            hargatot.setText("");
        } else {
            hitungTotalSatuan();
        }

    }//GEN-LAST:event_banykKeyReleased

    private void tmasukPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tmasukPropertyChange
        // TODO add your handling code here:
       
    }//GEN-LAST:event_tmasukPropertyChange

    private void tkeluarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tkeluarPropertyChange
        // TODO add your handling code here:
       
    }//GEN-LAST:event_tkeluarPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel b;
    private javax.swing.JTextField banyk;
    private javax.swing.JTextField bayarakhir;
    private javax.swing.JComboBox combopeg;
    private javax.swing.JComboBox comboproduk;
    private javax.swing.JTextField hargasat;
    private javax.swing.JTextField hargatot;
    private javax.swing.JTextField idtrans;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel keluar;
    private javax.swing.JTextField kontak;
    private javax.swing.JLabel kurang;
    private javax.swing.JLabel save;
    private javax.swing.JLabel tambah;
    private javax.swing.JTable tdetail;
    private com.toedter.calendar.JDateChooser tkeluar;
    private com.toedter.calendar.JDateChooser tmasuk;
    private javax.swing.JTable ttrans;
    // End of variables declaration//GEN-END:variables
}
