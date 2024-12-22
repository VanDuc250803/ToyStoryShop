import { createContext, useState } from "react";

type user = {
    name: string;
};

type AppContextType = {
  isAuthenticated: boolean;
  setIsAuthenticated: React.Dispatch<React.SetStateAction<boolean>>;

  user: {
    name: string;
  };
  setUser: React.Dispatch<React.SetStateAction<user>>;
};

const initialAppContext: AppContextType = {
  isAuthenticated: false,
  // sessionStorage.getItem("isAuthenticated") === "true",
  setIsAuthenticated: () => null,
  user: {
    name: "",
  },
  setUser: () => {},
};

export const AppContext = createContext<AppContextType>(initialAppContext);

export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(
    initialAppContext.isAuthenticated
  );
  const [user, setUser] = useState<any>({ name: "" });
  return (
    <AppContext.Provider
      value={{ isAuthenticated, setIsAuthenticated, user, setUser }}
    >
      {children}
    </AppContext.Provider>
  );
};
