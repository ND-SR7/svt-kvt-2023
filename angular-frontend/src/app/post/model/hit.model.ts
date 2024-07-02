import { PostIndex } from "./postIndex.model";

export class Hit {
    highlights: Map<string, string[]>;
    source: PostIndex;

    constructor(obj: {
        highlights?: Map<string, string[]>,
        source?: PostIndex,
    } = {}) {
        this.highlights = obj.highlights || null as unknown as Map<string, string[]>;
        this.source = obj.source || null as unknown as PostIndex;
    }
}
