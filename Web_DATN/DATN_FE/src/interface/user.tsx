export interface IUser{
    id:string;
    name:string;
    email:string;
    password:string
  }
  export type IUserRegister = Pick<IUser,'name'|'email'|'password'>
  export type IUserLogin = Pick<IUser,'email'|'password'>
  export type IUserCart = Pick<IUser,'id'>