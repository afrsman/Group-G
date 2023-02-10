import dayjs from 'dayjs/esm';

import { IPost, NewPost } from './post.model';

export const sampleWithRequiredData: IPost = {
  id: 35989,
  title: 'Fantastic',
  content: '../fake-data/blob/hipster.txt',
  date: dayjs('2023-02-09T11:29'),
};

export const sampleWithPartialData: IPost = {
  id: 93202,
  title: 'Seychelles',
  content: '../fake-data/blob/hipster.txt',
  date: dayjs('2023-02-09T03:42'),
};

export const sampleWithFullData: IPost = {
  id: 23240,
  title: 'recontextualize virtual copying',
  content: '../fake-data/blob/hipster.txt',
  date: dayjs('2023-02-09T05:23'),
};

export const sampleWithNewData: NewPost = {
  title: 'withdrawal',
  content: '../fake-data/blob/hipster.txt',
  date: dayjs('2023-02-09T06:54'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
