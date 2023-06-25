export class Reaction {
    _id: number;
    reactionType: string;
    timestamp: string;
    madeByUserId: number;
    onCommentId: number;
    onPostId: number;

    constructor(obj?: any) {
        this._id = obj && obj._id || null;
        this.reactionType = obj && obj.reactionType || null;
        this.timestamp = obj && obj.timestamp || null;
        this.madeByUserId = obj && obj.madeByUserId || null;
        this.onCommentId = obj && obj.onCommentId || null;
        this.onPostId = obj && obj.onPostId || null;
    }

    get id() {
        return this._id;
    }
}