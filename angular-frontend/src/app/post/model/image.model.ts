export class Image {
    _id: number;
    path: string;
    belongsToPostId: number;
    belongsToUserId: number;

    constructor(obj?: any) {
        this._id = obj && obj._id || null;
        this.path = obj && obj.path || null;
        this.belongsToPostId = obj && obj.belongsToPostId || null;
        this.belongsToUserId = obj && obj.belongsToUserId || null;
    }

    get id() {
        return this._id;
    }
}