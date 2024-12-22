import React, { useState, useEffect } from "react";
import axios from "axios";

interface Transaction {
  _id: string;
  amount: string;
  date: string;
  __v: number;
}

const Transaction: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [amount, setAmount] = useState<any>(0);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const response = await axios.get("http://localhost:28017/transactions");
        console.log("Dữ liệu nhận từ API:", response.data);
        setTransactions(response.data);
      } catch (error) {
        console.error("Lỗi khi lấy transactions:", error);
      }
    };
    fetchTransactions();
  }, []);

  const filteredTransactions = transactions.filter((transaction) => {
    const transactionDate = new Date(transaction.date);
    const start = startDate ? new Date(startDate) : null;
    const end = endDate ? new Date(endDate + "T23:59:59") : null;
  
    return (
      (!start || transactionDate >= start) &&
      (!end || transactionDate <= end)
    );
  });
 const totalAmount = filteredTransactions.reduce((sum, transaction) => {
    return sum + Number(transaction.amount);
  }, 0);
  return (
    <div style={styles.container}>
      <div style={styles.dateContainer}>
        <div style={{ marginTop: "20px", fontWeight: "bold", fontSize: "16px" }}>
          Tổng tiền:{" "}
          {totalAmount.toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND",
          })}
        </div>
        <label style={styles.label}>
          Ngày bắt đầu:
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            style={styles.dateInput}
          />
        </label>
        <label style={styles.label}>
          Ngày kết thúc:
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            style={styles.dateInput}
          />
        </label>
      </div>

      <div style={styles.innerContainer}>
        <h2 style={styles.heading}>Danh Sách Transaction</h2>
        <ul style={styles.list}>
          {filteredTransactions.length === 0 ? (
            <p style={styles.noData}>Không tìm thấy giao dịch nào</p>
          ) : (
            filteredTransactions.map((transaction) => (
              <li key={transaction._id} style={styles.listItem}>
                <div>
                  <strong>Số tiền nhận:</strong> {Number(transaction?.amount).toLocaleString()} VND
                </div>
                <div>
                  <strong>Ngày nhận:</strong> {new Date(transaction.date).toLocaleString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      })}
                </div>
              </li>
            ))
          )}
        </ul>
      </div>
    </div>
  );
};



const styles = {
  container: {
    maxWidth: "1000px",
    margin: "30px auto",
    padding: "10px",
  },
  navbar: {
    display: "flex",
    justifyContent: "flex-start",
    alignItems: "center",
    marginBottom: "20px",
  },
  addButton: {
    padding: "10px 15px",
    fontSize: "14px",
    backgroundColor: "#007BFF",
    color: "white",
    border: "none",
    borderRadius: "5px",
    cursor: "pointer",
    transition: "all 0.3s ease",
  },
  innerContainer: {
    padding: "20px",
    border: "1px solid #ddd",
    borderRadius: "10px",
    backgroundColor: "#ffffff",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
  },
  heading: {
    textAlign: "center" as const,
    marginBottom: "20px",
    fontSize: "20px",
    color: "#333",
    fontWeight: "bold",
  },
  list: {
    listStyle: "none",
    padding: "0",
    margin: "0",
  },
  listItem: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "10px 15px",
    marginBottom: "10px",
    border: "1px solid #ddd",
    borderRadius: "8px",
    backgroundColor: "#f8f9fa",
    fontSize: "14px",
    lineHeight: "1.5",
  },
  actionButtons: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "10px",
  },
  editButton: {
    padding: "10px 20px",
    backgroundColor: "#28a745",
    color: "white",
    border: "none",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "16px",
    transition: "background-color 0.3s ease",
  },
  editButtonHover: {
    backgroundColor: "#218838",
  },
  deleteButton: {
    padding: "10px 20px",
    backgroundColor: "#dc3545",
    color: "white",
    border: "none",
    borderRadius: "5px",
    cursor: "pointer",
    fontSize: "16px",
    transition: "background-color 0.3s ease",
  },
  deleteButtonHover: {
    backgroundColor: "#c82333",
  },
  noData: {
    textAlign: "center" as const,
    color: "#888",
    fontSize: "16px",
  },
  searchBar: {
    marginBottom: "20px",
    textAlign: "center" as const,
  },
  searchInput: {
    width: "80%",
    padding: "10px",
    fontSize: "16px",
    borderRadius: "8px",
    border: "1px solid #ccc",
  },
    dateContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '20px',
  },
  label: {
    fontSize: '16px',
    color: '#555',
    marginRight: '10px',
  },
  dateInput: {
    padding: '8px',
    borderRadius: '8px',
    border: '1px solid #ccc',
    width: '100%',
    fontSize: '16px',
  },
  loadingText: {
    textAlign: 'center' as 'center',
    fontSize: '18px',
    color: '#555',
  },
  analyticsContainer: {
    marginTop: '20px',
  },
  summaryContainer: {
    marginBottom: '20px',
  },
  productContainer: {
    marginTop: '20px',
  },
  chartContainer: {
    marginTop: '20px',
  },
  imageGallery: {
    display: 'flex',
    gap: '10px',
    marginBottom: '10px',
  },
  productImage: {
    width: '100px',
    height: '100px',
    objectFit: 'cover' as 'cover',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
  },
  noDataText: {
    textAlign: 'center' as 'center',
    fontSize: '18px',
    color: '#888',
  },
};




export default Transaction;


