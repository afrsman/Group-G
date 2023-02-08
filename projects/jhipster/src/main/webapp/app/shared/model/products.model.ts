export interface IProducts {
  id?: number;
  name?: string;
  price?: number;
  size?: string | null;
}

export const defaultValue: Readonly<IProducts> = {};
