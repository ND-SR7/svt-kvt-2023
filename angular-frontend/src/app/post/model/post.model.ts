import { Image } from "./image.model";

export class Post {
    _id: number;
    content: string;
    creationDate: string;
    postedByUserId: number;
    images: Image[] = [];
    belongsToGroupId: number;
    file: File;

    constructor(obj: {
        _id?: number,
        content?: string,
        creationDate?: string,
        postedByUserId?: number,
        images?: Image[],
        belongsToGroupId?: number,
        file?: File
    } = {}) {
        this._id = obj._id || null as unknown as number;
        this.content = obj.content || null as unknown as string;
        this.creationDate = obj.creationDate || null as unknown as string;
        this.postedByUserId = obj.postedByUserId || null as unknown as number;
        this.images = obj.images || [];
        this.belongsToGroupId = obj.belongsToGroupId || null as unknown as number;
        this.file = obj.file || null as unknown as File;
    }

    get id() {
        return this._id;
    }
}
