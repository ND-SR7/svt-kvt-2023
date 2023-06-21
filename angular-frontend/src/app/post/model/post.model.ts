import { Image } from "./image.model";

export class Post {
    _id: number;
    content: String;
    creationDate: String;
    postedByUserId: number;
    images: Image[] = [];

    constructor(obj?: any) {
        this._id = obj && obj._id || null;
        this.content = obj && obj.content || null;
        this.creationDate = obj && obj.creationDate || null;
        this.postedByUserId = obj && obj.postedByUserId || null;
    }

    get id() {
        return this._id;
    }
}
