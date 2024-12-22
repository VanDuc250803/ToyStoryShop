import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyC1Jt0nZsQZ8-KGJ7UVZofzhUlzRPAI3RQ",
  authDomain: "datn-toystoryshop-94692.firebaseapp.com",
  projectId: "datn-toystoryshop-94692",
  storageBucket: "datn-toystoryshop-94692.firebasestorage.app",
  messagingSenderId: "392434089726",
  appId: "1:392434089726:web:35168d4e14ab6865e9efaf",
  measurementId: "G-31MBH50TPS"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
